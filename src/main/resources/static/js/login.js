document.addEventListener("DOMContentLoaded", () => {
    const signupBtn = document.getElementById("signupBtn");

    // 회원가입 버튼 클릭 이벤트
    if (signupBtn) {
        signupBtn.addEventListener("click", () => {
            const showSignup = signupBtn.dataset.showSignup || "false";
            if (showSignup.toLowerCase() === 'true') {
                window.location.href = "/signup";
            } else {
                alert("가입은 관리자에게 문의하세요");
            }
        });
    }

    // 회원가입 완료 후 로그인 화면에서 메시지 띄우기
    const signupSuccess = "{{signupSuccess}}" || "false";
    if (signupSuccess.toLowerCase() === "true") {
        alert("회원가입이 완료되었습니다!");
        window.location.href = "/login";
    }
});
