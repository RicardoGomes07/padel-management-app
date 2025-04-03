const API_BASE_URL = "http://localhost:9000/"


function getHome(mainContent) {
    h1("Welcome to the Home Page")
    a("Go to Clubs", "#clubs")
    mainContent.replaceChildren(h1)
}
