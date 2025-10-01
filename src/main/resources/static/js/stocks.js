document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('.dropdown').forEach(drop => {
        const button = drop.querySelector('button');
        const menu = drop.querySelector('.dropdown_menu');

        button.addEventListener('click', () => {
            // 다른 드롭다운이 열려있으면 닫기
            document.querySelectorAll('.dropdown_menu').forEach(m => {
                if (m !== menu) m.style.display = 'none';
            });

            menu.style.display = menu.style.display === 'block' ? 'none' : 'block';
        });
    });
});
