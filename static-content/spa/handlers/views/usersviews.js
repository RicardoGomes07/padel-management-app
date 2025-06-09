import Html from "../../dsl/htmlfuns.js";
import pagination from "./pagination.js";
import uriManager from "../../managers/uriManager.js";
import { redirectTo } from "../../router.js";

const { div, a, ul, li, p, formElement, button } = Html;
const { createPaginationLinks } = pagination
const { getUserRentalsUri, getUserProfileUri, getRentalDetailsUri, loginUri, signUpUri, logoutUri } = uriManager

function renderUserRentalsView(contentHeader, content, rentals, count, username,  uid, page) {
    const currHeader = contentHeader.textContent
    const header = "Rentals of " + username

    const backLink = div(a("Back", getUserProfileUri(uid)))

    const rentalList = rentals.length > 0
        ? ul(
            ...rentals.map(rental =>
                li(
                    a(`${rental.date} ${rental.initialHour} to ${rental.finalHour}`,
                        getRentalDetailsUri(rental.cid, rental.crid, rental.rid)
                    )
                )
            )
        )
        : p("No rentals found")

    const info = div(rentalList)
    const navigation = createPaginationLinks(getUserRentalsUri(uid, page), count, page)

    if (currHeader !== header) contentHeader.replaceChildren(header)
    content.replaceChildren(backLink, info, navigation)
}

function renderUserDetailsView(contentHeader, content, user) {
    const header = "User Info"
    const info = ul(
        li("Name: " + user.name),
        li("Email: " + user.email),
        li(a("User Rentals ", getUserRentalsUri(user.uid))),
    )

    contentHeader.replaceChildren(header)
    content.replaceChildren(info)
}

function renderSignUpView(contentHeader, content, handleSubmit) {
    const header = "Sign Up"
    const info = p("Please sign up to access our website.")

    const fields = [
        { id: "name", label: "Name", type: "text", required: true },
        { id: "email", label: "Email", type: "email", required: true },
        { id: "password", label: "Password", type: "password", required: true },
    ]

    const form = li(
        formElement(fields, handleSubmit, {
            className: "form",
            submitText: "Sign Up"
        })
    )

    contentHeader.replaceChildren(header)
    content.replaceChildren(info, form)
}

function renderLoginView(contentHeader, content, handleSubmit) {
    const header = "Login"
    const info = p("Please login to access our website.")

    const fields = [
        { id: "email", label: "Email", type: "email", required: true },
        { id: "password", label: "Password", type: "password", required: true },
    ]

    const form = li(
        formElement(fields, handleSubmit, {
            className: "organized-form",
            submitText: "Login"
        })
    )

    contentHeader.replaceChildren(header)
    content.replaceChildren(info, form)
}

function logoutButton() {
    return button("Logout", () => {
        redirectTo(logoutUri())
    }, {
        className: "btn btn-danger mt-2",
        id: "logout-button",
        style: {
            position: "fixed",
            top: "20px",
            right: "20px",
            zIndex: "1000"
        }
    })
}

function signUpAndLoginButtons() {
    return div(
        a("Sign Up", signUpUri()),
        a("Login", loginUri())
    );
}

const usersViews = {
    renderUserRentalsView,
    renderUserDetailsView,
    renderSignUpView,
    renderLoginView,
    logoutButton,
    signUpAndLoginButtons
}

export default usersViews;