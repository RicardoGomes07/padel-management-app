import usersViews from "../handlers/views/usersviews.js";
import htmlfuns from "../dsl/htmlfuns.js";

const {div} = htmlfuns
const TOKEN_KEY = "userToken"
const USER_ID_KEY = "uid"
const authStatusContent = document.getElementById("authContent") || div() // Fallback to a div if the element is not found to be used in tests
const { signUpAndLoginButtons, logoutButton } = usersViews


function getSessionItem(key) {
    return sessionStorage.getItem(key)
}

function setSessionItem(key, value) {
    sessionStorage.setItem(key, value)
}

function removeSessionItem(key) {
    sessionStorage.removeItem(key)
}

export function getCurrToken() {
    return getSessionItem(TOKEN_KEY)
}

export function authenticated() {
    return getCurrToken() !== null
}

export function setUserInfo(userInfo) {
    if (userInfo) {
        setSessionItem(TOKEN_KEY, userInfo.token)
        setSessionItem(USER_ID_KEY, userInfo.uid)
    } else {
        removeSessionItem(TOKEN_KEY)
        removeSessionItem(USER_ID_KEY)
    }
}

export const userAuthenticationManager = ((authContent) => {
    let currentUid = getSessionItem(USER_ID_KEY)
    if (currentUid) {
        authContent.replaceChildren(logoutButton())
    } else {
        authContent.replaceChildren(signUpAndLoginButtons())
    }

    return {
        stateChanged() {
            const newUid = getSessionItem(USER_ID_KEY)
            const changed = newUid !== currentUid
            currentUid = newUid
            return changed
        },
        updateContent() {
           updateAuthUI(authContent)
        }
    }
})(authStatusContent)


function updateAuthUI(target) {
    if (authenticated()) {
        target.replaceChildren(logoutButton())
    } else {
        target.replaceChildren(signUpAndLoginButtons())
    }
}
