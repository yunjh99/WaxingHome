document.addEventListener("DOMContentLoaded", function () {
    const pathParts = window.location.pathname.split("/").filter(Boolean);
    const eventId = pathParts[pathParts.length - 1]; // /event/update/{id} 같은 경우 {id}

    // ===== DOM =====
    const form = document.getElementById("eventwriteform");

    const thumbnailInputText = document.getElementById("thumbnailInput");
    const bodyImageInputText = document.getElementById("bodyImageInput");

    const thumbnailInputFile = document.getElementById("thumbnail");
    const bodyImageInputFile = document.getElementById("bodyImage");

    const existingThumbnailList = document.getElementById("existingThumbnailList");
    const existingBodyImageList = document.getElementById("existingBodyImageList");

    const thumbWarning = document.getElementById("thumbnail-warning");
    const bodyWarning = document.getElementById("bodyImage-warning");

    // ===== State =====
    let selectedThumbnailFile = null; // File
    let selectedBodyImageFile = null; // File

    // 기존 파일 삭제 의도 (서버가 이 값을 받게 만들고 싶다면)
    let deleteThumbnail = false;
    let deleteBodyImage = false;

    // ===== Util =====
    function showWarning(el, msg) {
        if (!el) return;
        el.textContent = msg;
        el.style.display = msg ? "block" : "none";
    }

    function clearList(ul) {
        if (!ul) return;
        ul.innerHTML = "";
    }

    function createFileItem({ label, onDelete }) {
        const wrap = document.createElement("div");
        wrap.classList.add("file-item");

        const nameSpan = document.createElement("span");
        nameSpan.textContent = label;
        wrap.appendChild(nameSpan);

        const delBtn = document.createElement("button");
        delBtn.type = "button";
        delBtn.textContent = "삭제";
        delBtn.classList.add("delete-btn");
        delBtn.addEventListener("click", onDelete);
        wrap.appendChild(delBtn);

        return wrap;
    }

    // ===== 기존 이미지 표시 =====
    // API 응답 포맷이 다를 수 있어서 최대한 유연하게 처리
    function normalizeImageResponse(data) {

        if (Array.isArray(data) && data.length && typeof data[0] === "object") {
            const thumb = data.find(x => (x.type || x.imageType) === "THUMBNAIL");
            const body = data.find(x => (x.type || x.imageType) === "BODY");
            return {
                thumbnailUrl: thumb ? (thumb.url || thumb.imageUrl) : null,
                bodyImageUrl: body ? (body.url || body.imageUrl) : null,
            };
        }


        return { thumbnailUrl: null, bodyImageUrl: null };
    }

    function renderExistingThumbnail(url) {
        clearList(existingThumbnailList);
        if (!url) return;

        const fileName = url.split("/").pop();

        const item = createFileItem({
            label: fileName,
            onDelete: function () {
                // 화면에서 제거 + 삭제 플래그 세팅
                clearList(existingThumbnailList);
                thumbnailInputText.value = "";
                deleteThumbnail = true;
            }
        });

        existingThumbnailList.appendChild(item);
        // 텍스트에도 표시(원하면 제거 가능)
        thumbnailInputText.value = fileName;
    }

    function renderExistingBodyImage(url) {
        clearList(existingBodyImageList);
        if (!url) return;

        const fileName = url.split("/").pop();

        const item = createFileItem({
            label: fileName,
            onDelete: function () {
                clearList(existingBodyImageList);
                bodyImageInputText.value = "";
                deleteBodyImage = true;
            }
        });

        existingBodyImageList.appendChild(item);
        bodyImageInputText.value = fileName;
    }

    // 수정 화면이면 기존 이미지 조회
    // (eventId가 "write" 같은 문자열이면 스킵)
    if (eventId && eventId !== "write") {
        // ⚠️ 여기 URL은 네 서버 매핑에 맞게 수정해야 할 수 있음
        fetch(`/event/api/event/${eventId}/images`)
            .then(res => {
                if (!res.ok) throw new Error("Failed to fetch images");
                return res.json();
            })
            .then(data => {
                const { thumbnailUrl, bodyImageUrl } = normalizeImageResponse(data);
                renderExistingThumbnail(thumbnailUrl);
                renderExistingBodyImage(bodyImageUrl);
            })
            .catch(err => console.error("Error fetching event images:", err));
    }

    // ===== 파일 선택 핸들러 =====
    thumbnailInputFile.addEventListener("change", function (e) {
        const file = e.target.files && e.target.files[0];
        showWarning(thumbWarning, "");

        if (!file) return;
        if (!file.type.startsWith("image/")) {
            showWarning(thumbWarning, "이미지 파일만 업로드 가능합니다.");
            thumbnailInputFile.value = "";
            return;
        }

        selectedThumbnailFile = file;
        deleteThumbnail = false; // 새로 선택했으니 기존 삭제 플래그 해제

        // 기존 목록은 “선택 파일”로 교체
        clearList(existingThumbnailList);
        const item = createFileItem({
            label: file.name,
            onDelete: function () {
                selectedThumbnailFile = null;
                thumbnailInputFile.value = "";
                thumbnailInputText.value = "";
                clearList(existingThumbnailList);
            }
        });
        existingThumbnailList.appendChild(item);

        thumbnailInputText.value = file.name;
    });

    bodyImageInputFile.addEventListener("change", function (e) {
        const file = e.target.files && e.target.files[0];
        showWarning(bodyWarning, "");

        if (!file) return;
        if (!file.type.startsWith("image/")) {
            showWarning(bodyWarning, "이미지 파일만 업로드 가능합니다.");
            bodyImageInputFile.value = "";
            return;
        }

        selectedBodyImageFile = file;
        deleteBodyImage = false;

        clearList(existingBodyImageList);
        const item = createFileItem({
            label: file.name,
            onDelete: function () {
                selectedBodyImageFile = null;
                bodyImageInputFile.value = "";
                bodyImageInputText.value = "";
                clearList(existingBodyImageList);
            }
        });
        existingBodyImageList.appendChild(item);

        bodyImageInputText.value = file.name;
    });

    // ===== 제출 =====
    form.addEventListener("submit", function (e) {
        e.preventDefault();

        const formData = new FormData(form);

        // 파일은 1개씩만
        // (DTO 필드명: thumbnail, bodyImage)
        // 기존에 name이 같으면 formData에 이미 들어가 있을 수도 있으니 삭제 후 넣는 게 안전
        formData.delete("thumbnail");
        formData.delete("bodyImage");

        if (selectedThumbnailFile) formData.append("thumbnail", selectedThumbnailFile);
        if (selectedBodyImageFile) formData.append("bodyImage", selectedBodyImageFile);

        // 기존 파일 삭제를 서버에 알리고 싶다면 (서버에서 받을 파라미터명 맞춰서)
        // 예: deleteThumbnail=true, deleteBodyImage=true
        if (deleteThumbnail) formData.append("deleteThumbnail", "true");
        if (deleteBodyImage) formData.append("deleteBodyImage", "true");

        fetch(form.action, {
            method: "POST",
            body: formData
        })
            .then(res => {
                if (!res.ok) throw new Error("Network response was not ok");
                return res.text();
            })
            .then(() => {
                window.location.href = "/community/events";
            })
            .catch(err => console.error("Error:", err));
    });
});
