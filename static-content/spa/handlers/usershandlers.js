import { request } from "../router.js"
import {ELEMS_PER_PAGE} from "./views/pagination.js"
import usersViews from "./views/usersviews.js"
import usersRequests from "./requests/usersrequests.js"
import errorsViews from "./views/errorsview.js";
import { setUserInfo } from "../managers/userAuthenticationContext.js";
import uriManager from "../managers/uriManager.js";

const { path, query } = request
const { renderUserRentalsView, renderUserDetailsView, renderSignUpView, renderLoginView } = usersViews
const { fetchUserRentals, fetchUserDetails, createUser, loginUser} = usersRequests
const { errorView } = errorsViews
const { homeUri } = uriManager


async function getUserRentals(contentHeader, content) {
    const uid = path("uid")
    const page = Number(query("page")) || 1
    const skip = (page - 1) * ELEMS_PER_PAGE

    const rsp = await fetchUserRentals(uid, skip, ELEMS_PER_PAGE).then(result => result.data)
    const rentals = rsp.items.rentals.slice(0, ELEMS_PER_PAGE) ?? []
    const count = rsp.count

    const userName = await fetchUserDetails(Number(uid)).then(user => user.data.name)

    renderUserRentalsView(
        contentHeader,
        content,
        rentals,
        count,
        userName,
        uid,
        page
    )
}

async function getUserDetails(contentHeader, content){
    const userId = path("uid")

    const result = await fetchUserDetails(userId)

    if (result.status !== 200) errorView(contentHeader, content, homeUri(),result.data)
    else renderUserDetailsView(contentHeader, content, result.data)
}

function signUp(contentHeader, content){
    const handleSubmit = async function(e) {
        e.preventDefault()
        const name = e.target.querySelector("#name").value
        const email = e.target.querySelector("#email").value
        const password = e.target.querySelector("#password").value

        const result = await createUser(name, email, password)

        if (result.status !== 201) {
            errorView(contentHeader, content, homeUri(), result.data)
        } else {
            const login = await loginUser(email, password)
            if (login.status !== 200) {
                errorView(contentHeader, content, homeUri(), login.data)
            }else{
                console.log("User logged in successfully, data:", login.data)
                setUserInfo(login.data)
                window.history.back() // Redirect to the previous page
            }
        }
    }

    renderSignUpView(contentHeader, content, handleSubmit)
}

function login(contentHeader, content) {
    const handleSubmit = async function(e) {
        e.preventDefault()
        const email = e.target.querySelector("#email").value
        const password = e.target.querySelector("#password").value

        const result = await loginUser(email, password)

        if (result.status !== 200) {
            errorView(contentHeader, content, homeUri(), result.data)
        } else {
            setUserInfo(result.data)
            window.history.back() // Redirect to the previous page
        }
    }

    renderLoginView(contentHeader, content, handleSubmit)
}

const userHandlers = {
    getUserDetails,
    getUserRentals,
    signUp,
    login,
}

export default userHandlers