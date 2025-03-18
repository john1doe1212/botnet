document.addEventListener("DOMContentLoaded", function () {
    const loginForm = document.getElementById("loginForm");

    if (loginForm) {
        loginForm.addEventListener("submit", function (event) {
            event.preventDefault();
            const username = document.getElementById("username").value;
            const password = document.getElementById("password").value;

            fetch("/login", {
                method: "POST",
                headers: { "Content-Type": "application/x-www-form-urlencoded" },
                body: `username=${encodeURIComponent(username)}&password=${encodeURIComponent(password)}`
            })
                .then(response => {
                    if (response.ok) {
                        document.cookie = `username=${username}; path=/`;
                        window.location.href = "/dashboard.html";
                    } else {
                        alert("Invalid username or password!");
                    }
                })
                .catch(() => {
                    alert("Login failed. Please try again.");
                });
        });
    }

    const signupForm = document.getElementById("signupForm");

    if (signupForm) {
        signupForm.addEventListener("submit", function (event) {
            event.preventDefault();
            const newUsername = document.getElementById("newUsername").value;
            const newPassword = document.getElementById("newPassword").value;

            fetch("/signup", {
                method: "POST",
                headers: { "Content-Type": "application/x-www-form-urlencoded" },
                body: `newUsername=${encodeURIComponent(newUsername)}&newPassword=${encodeURIComponent(newPassword)}`
            })
                .then(response => {
                    if (response.ok) {
                        alert("Account created! You can now log in.");
                        window.location.href = "/index.html";
                    } else {
                        alert("Signup failed. Username may already be taken.");
                    }
                })
                .catch(() => {
                    alert("Signup failed. Please try again.");
                });
        });
    }

    function getCookie(name) {
        let match = document.cookie.match(new RegExp('(^| )' + name + '=([^;]+)'));
        return match ? match[2] : null;
    }

    const username = getCookie("username");

    if (document.getElementById("welcomeMessage") && username) {
        document.getElementById("welcomeMessage").innerText = "Welcome, " + username + "!";
    }

    document.querySelectorAll(".link-button").forEach(button => {
        button.addEventListener("click", function () {
            const text = this.innerText.trim();

            if (text === "Ethical Hacking Tools (Coming Soon)" || text === "Paid Area") {
                alert("Please contact ja-ha0 on snapchat with proof of the payment before getting the advanced tools!!");
            } else if (text === "Game Hacks") {
                window.location.href = "/game-hacks.html"; // Fixed path
            }
        });
    });

    const logoutButton = document.querySelector(".signout");
    if (logoutButton) {
        logoutButton.addEventListener("click", function () {
            document.cookie = "username=; Max-Age=0";
            window.location.href = "/index.html";
        });
    }

    const upgradeButton = document.querySelector(".upgrade");
    if (upgradeButton) {
        upgradeButton.addEventListener("click", function () {
            document.getElementById("upgradeModal").style.display = "flex";
        });
    }

    const closeModal = document.querySelector(".close");
    if (closeModal) {
        closeModal.addEventListener("click", function () {
            document.getElementById("upgradeModal").style.display = "none";
        });
    }
});

