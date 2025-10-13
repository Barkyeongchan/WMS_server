document.getElementById("signupForm").addEventListener("submit", async (e) => {
  e.preventDefault();

  const id = document.getElementById("userId").value.trim();
  const pw = document.getElementById("userPw").value.trim();
  const name = document.getElementById("userName").value.trim();

  if (!id || !pw || !name || !email) {
    alert("모든 필드를 입력해주세요.");
    return;
  }

  try {
    const response = await fetch("/signup", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ id, pw, name })
    });

    const result = await response.json();

    if (result.success) {
      alert("회원가입이 완료되었습니다!");
      window.location.href = "/login";
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
