document.addEventListener("DOMContentLoaded", function () {
    // 회원가입 버튼 클릭
    const signupBtn = document.getElementById("signupBtn");
    signupBtn.addEventListener("click", function () {
        // /signup 페이지로 이동
        window.location.href = "/signup";
    });
});
