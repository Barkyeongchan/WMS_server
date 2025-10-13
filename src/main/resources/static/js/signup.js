document.getElementById("signupForm").addEventListener("submit", async (e) => {
  e.preventDefault();

  const id = document.getElementById("userid").value.trim();
  const pw = document.getElementById("password").value.trim();
  const name = document.getElementById("username").value.trim();

  if (!id || !pw || !name) {
    alert("모든 필드를 입력해주세요.");
    return;
  }

   // FormData 방식으로 전송 (기존 @RequestParam과 호환)
   const formData = new FormData();
   formData.append("userid", id);
   formData.append("username", name);
   formData.append("password", pw);

  try {
    const response = await fetch("/signup", {
      method: "POST",
      body: formData
    });

    const result = await response.json();

    if (result.success) {
      alert("회원가입이 완료되었습니다!");
      window.location.href = response.url;
    } else {
      alert(result.message);
    }
  } catch (error) {
    alert("회원가입 중 오류가 발생했습니다.");
    console.error(error);
  }
});

document.getElementById("backToLogin").addEventListener("click", () => {
  window.location.href = "/login";
});
