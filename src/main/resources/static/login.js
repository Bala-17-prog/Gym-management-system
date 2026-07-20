function validateLogin() {
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;

    if (username === "powergym" && password === "power123") {
        window.location.href = "index.html"; // redirect to index page
    } else {
        alert("Invalid username or password");
    }
}
