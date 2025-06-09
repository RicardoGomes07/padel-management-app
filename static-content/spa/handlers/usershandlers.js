import { request } from "../router.js"
import {ELEMS_PER_PAGE} from "./views/pagination.js"
import usersViews from "./views/usersviews.js"
import usersRequests from "./requests/usersrequests.js"
import errorsViews from "./views/errorsview.js";
import { setUserInfo } from "../managers/userAuthenticationManager.js";
import uriManager from "../managers/uriManager.js";
import errorManager from "../managers/errorManager.js";
import { hashPassword } from "../managers/passwordCodificationManager.js";
import { redirectTo } from "../router.js";

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
    if (rsp.status !== 200) {
        errorManager.store(errorView(rsp.data))
        redirectTo(homeUri())
        return
    }
    const rentals = rsp.items.rentals.slice(0, ELEMS_PER_PAGE) ?? []
    const count = rsp.count

    const userName = await fetchUserDetails(Number(uid)).then(user => user.data.name)
    if (userName.status !== 200) {
        errorManager.store(errorView(userName.data))
        redirectTo(homeUri())
        return
    }

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

    if (result.status === 200) {
        renderUserDetailsView(contentHeader, content, result.data)
    } else {
        errorManager.store(errorView(result.data))
        redirectTo(homeUri())
    }
}

function signUp(contentHeader, content){
    const handleSubmit = async function(e) {
        e.preventDefault()
        const name = e.target.querySelector("#name").value
        const email = e.target.querySelector("#email").value
        const password = e.target.querySelector("#password").value

        const hashedPassword = await hashPassword(password)

        const result = await createUser(name, email, hashedPassword)

        if (result.status === 201) {
            const login = await loginUser(email, hashedPassword)
            if (login.status === 200) {
                setUserInfo(login.data)
                redirectTo(homeUri())
            } else{
                errorManager.store(errorView(login.data)).render()
            }
        } else {
            errorManager.store(errorView(result.data)).render()
        }
    }

    renderSignUpView(contentHeader, content, handleSubmit)
}

function login(contentHeader, content) {
    const handleSubmit = async function(e) {
        e.preventDefault()
        const email = e.target.querySelector("#email").value.trim()
        const password = e.target.querySelector("#password").value.trim()

        const hashedPassword = await hashPassword(password)

        const result = await loginUser(email, hashedPassword)

        if (result.status === 200) {
            setUserInfo(result.data)
            redirectTo(homeUri())
        } else {
            errorManager.store(errorView(result.data)).render()
        }
    }

    renderLoginView(contentHeader, content, handleSubmit)
}

async function logout() {
    await usersRequests.logoutUser()
    setUserInfo(null)
    redirectTo(homeUri())
}

const userHandlers = {
    getUserDetails,
    getUserRentals,
    signUp,
    login,
    logout,
}

export default userHandlers