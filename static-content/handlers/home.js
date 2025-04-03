const API_BASE_URL = "http://localhost:9000/"


function getHome(mainContent) {
    const h1 = document.createElement("h1")
    const text = document.createTextNode("Welcome to the Home Page")
    h1.appendChild(text)
    mainContent.replaceChildren(h1)
}
