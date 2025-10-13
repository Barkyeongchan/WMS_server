// 회원가입 form submit 처리 (HTML form action 사용)
const signupForm = document.getElementById("signupForm");
if (signupForm) {
    signupForm.addEventListener("submit", function (e) {
        const id = document.getElementById("userid").value.trim();
        const pw = document.getElementById("password").value.trim();
        const name = document.getElementById("username").value.trim();

        if (!id || !pw || !name) {
            e.preventDefault(); // 입력 안되면 submit 막기
            alert("모든 필드를 입력해주세요.");
        }
    });
}

// 로그인으로 돌아가기 버튼
const backBtn = document.getElementById("backToLogin");
if (backBtn) {
    backBtn.addEventListener("click", () => {
        window.location.href = "/login";
    });
}
