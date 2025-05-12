export default function userAuthenticationManager(){
    let currentUserToken  = null

    return {
        getCurrToken() {
            return currentUserToken
        },
        setCurrToken(token) {
           currentUserToken = token
              return this
        }
    }
}