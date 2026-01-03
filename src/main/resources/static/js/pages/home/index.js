document.addEventListener('DOMContentLoaded', function() {
    const slides = document.querySelectorAll('.slide');
    const dots = document.querySelectorAll('.dot');
    let currentIndex = 0;
    let slideInterval;

    function showSlide(index) {
        slides[currentIndex].classList.remove('active');
        dots[currentIndex].classList.remove('active');
        currentIndex = index;
        slides[currentIndex].classList.add('active');
        dots[currentIndex].classList.add('active');
    }

    function showNextImage() {
        let nextIndex = (currentIndex + 1) % slides.length;
        showSlide(nextIndex);
    }

    dots.forEach(dot => {
        dot.addEventListener('click', function() {
            let index = parseInt(this.getAttribute('data-index'));
            clearInterval(slideInterval);
            showSlide(index);
            slideInterval = setInterval(showNextImage, 6000);
        });
    });

    slideInterval = setInterval(showNextImage, 6000); // 5초마다 다음 이미지로 전환
});

document.addEventListener('DOMContentLoaded', () => {
    const items = document.querySelectorAll('.overlayimg-item');

    // 초기 설정: 첫 번째 아이템 활성화
    items[2].classList.add('active');
    items[2].style.flex = '5';

    items.forEach(item => {
        item.addEventListener('click', () => {
            // 모든 아이템에서 active 클래스 제거 및 너비 초기화
            items.forEach(i => {
                i.classList.remove('active');
                i.style.flex = '0.7';
            });

            // 클릭한 아이템에 active 클래스 추가 및 너비 조정
            item.classList.add('active');
            item.style.flex = '5'; // 클릭된 아이템의 너비를 더 크게 설정
        });
    });
});

// 스크롤 시 .image-item이 나타나는 효과
document.addEventListener("DOMContentLoaded", function () {
    const elementsToShow = document.querySelectorAll('.image-item, .middle-middle h1');

    function handleScroll() {
        elementsToShow.forEach(item => {
            const rect = item.getBoundingClientRect();
            if (rect.top < window.innerHeight * 0.9) {
                item.classList.add('show');
            }
        });
    }

    window.addEventListener('scroll', handleScroll);
    handleScroll(); // 페이지 로딩 시 한 번 실행
});