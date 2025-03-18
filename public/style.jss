body {
    background-color: #000;
    color: #fff;
    font-family: Arial, sans-serif;
    margin: 0;
    padding: 0;
}

.container {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 100vh;
    gap: 20px;
}

.link-button {
    background: #2a2a2a;
    border: none;
    color: #fff;
    padding: 15px 30px;
    border-radius: 5px;
    cursor: pointer;
    transition: background 0.3s;
    width: 300px;
}

.link-button:hover {
    background: #3a3a3a;
}

.signout {
    position: absolute;
    top: 20px;
    right: 20px;
    background: #ff4444;
    border: none;
    color: white;
    padding: 10px 20px;
    border-radius: 5px;
    cursor: pointer;
}