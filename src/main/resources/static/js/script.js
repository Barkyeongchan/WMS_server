document.addEventListener('DOMContentLoaded', () => {
    // WebSocket 연결
    const ws = new WebSocket("ws://192.168.1.6:8080/ws"); // 웹서버 IP:포트 확인

    ws.onopen = () => { console.log("WebSocket 연결 성공"); };

    ws.onmessage = (event) => {
        console.log("받은 메시지:", event.data);
        const data = JSON.parse(event.data);
        if (data.topic && data.topic.includes("turtle1/pose")) {
            document.getElementById("pose").innerText = JSON.stringify(data.msg, null, 2);
        }
    };

    ws.onerror = (err) => { console.error("WebSocket 에러:", err); };
    ws.onclose = () => { console.log("WebSocket 연결 종료"); };

    const toggleBtn = document.getElementById("togglebtn");
    const sidebar = document.getElementById("sidebar");
    const mainScreen = document.getElementById("main_screen");

    toggleBtn.addEventListener("click", () => {
        sidebar.classList.toggle("closed");
        mainScreen.classList.toggle("expanded");
    });

    const userIcon = document.getElementById("user_icon");
    const userMenu = document.getElementById("user_menu");

    userIcon.addEventListener("click", () => {
        userMenu.style.display = userMenu.style.display === "block" ? "none" : "block";
    });

    document.addEventListener("click", (event) => {
        if (!userIcon.contains(event.target) && !userMenu.contains(event.target)) {
            userMenu.style.display = "none";
        }
    });
});
