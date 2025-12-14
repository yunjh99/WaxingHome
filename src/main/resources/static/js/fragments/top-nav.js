(() => {
    const topNav = document.querySelector('.top-nav');

    const updateNavOnScroll = () => {
        topNav.classList.toggle('scrolled', window.scrollY > 0);
    };

    window.addEventListener('scroll', updateNavOnScroll);
    updateNavOnScroll();
})();

// overlay -----------------------------------------------------------------------------
function toggleMenu(button) {
    const overlay = document.querySelector('.overlay');
    const isOpened = button.classList.toggle('opened');
    button.setAttribute('aria-expanded', isOpened);

    // overlay의 상태에 따라 클래스 추가 또는 제거
    if (isOpened) {
        overlay.classList.add('open'); // overlay 보이게 설정
    } else {
        overlay.classList.remove('open'); // overlay 숨김
    }
}