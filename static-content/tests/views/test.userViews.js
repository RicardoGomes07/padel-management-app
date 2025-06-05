import usersViews from "../../spa/handlers/views/usersviews.js";
const assert = window.chai.assert

describe("UsersViews", function () {
    let contentHeader, content;
    
    beforeEach(function () {
        contentHeader = document.createElement('div');
        content = document.createElement('div');
    });

    describe("renderUserDetailsView", function () {
        context("when rendering user details", function () {
             it("should render user details with correct info and links", function () {
                const user = {uid: "1", name: "Ric", email: "riczao@gmail.com" };
                usersViews.renderUserDetailsView(contentHeader, content, user);
                assert.strictEqual(contentHeader.textContent, "User Info");

                const listItems = content.querySelectorAll("ul li");
                assert.strictEqual(listItems.length, 3);
                assert.strictEqual(listItems[0].textContent, "Name: Ric");
                assert.strictEqual(listItems[1].textContent, "Email: riczao@gmail.com");

                const rentalLink = listItems[2].querySelector("a");
                assert.strictEqual(rentalLink.textContent, "User Rentals ");
                assert.strictEqual(rentalLink.getAttribute("href"), `#users/1/rentals?page=1` );
            });
        });
    });

    describe("renderUserRentalsView", function () {
        context("when rendering user rentals", function () {
            it("should render user rentals with correct links and pagination", function () {
                const rentals = [
                    { rid: 1, cid: 10, crid: 5, date: "2025-05-25", initialHour: "10", finalHour: "12" },
                    { rid: 2, cid: 10, crid: 5, date: "2025-05-26", initialHour: "14", finalHour: "16" }
                ];
                const username = "Ric";
                const uid = 1;

                usersViews.renderUserRentalsView(contentHeader, content, rentals, 2 , username, uid, 1);
                assert.strictEqual(contentHeader.textContent, "Rentals of Ric");

                const links = content.querySelectorAll("ul li a");
                assert.strictEqual(links.length, 2);

                assert.strictEqual(links[0].textContent, "2025-05-25 10 to 12");
                assert.strictEqual(links[0].getAttribute("href"), "#clubs/10/courts/5/rentals/1");

                assert.strictEqual(links[1].textContent, "2025-05-26 14 to 16");
                assert.strictEqual(links[1].getAttribute("href"), "#clubs/10/courts/5/rentals/2");

                const backLink = content.querySelector("div a");
                assert.strictEqual(backLink.textContent, "Back");
                assert.strictEqual(backLink.getAttribute("href"), "#users/1");
            });

            it("should display message when no rentals are found", function () {
                const rentals = [];
                const username = "Ric";
                const uid = 1;

                usersViews.renderUserRentalsView(contentHeader, content, rentals, 0 ,username, uid, 1);
                assert.strictEqual(contentHeader.textContent, "Rentals of Ric");

                const message = content.querySelector("p");
                assert.strictEqual(message.textContent, "No rentals found");

                const backLink = content.querySelector("div a");
                assert.strictEqual(backLink.textContent, "Back");
                assert.strictEqual(backLink.getAttribute("href"), "#users/1");
            });
        });
    });
    describe("renderSignUpView", function () {
        context("when rendering sign up view", function () {
            it("should render sign up form with correct fields and submit button", function () {
                const handleSubmit = function() {};
                usersViews.renderSignUpView(contentHeader, content, handleSubmit);

                assert.strictEqual(contentHeader.textContent, "Sign Up");
                const info = content.querySelector("p");
                assert.strictEqual(info.textContent, "Please sign up to access our website.");

                const form = content.querySelector("form");
                assert.isNotNull(form);

                const fields = form.querySelectorAll("input");
                assert.strictEqual(fields.length, 3);
                assert.strictEqual(fields[0].id, "name");
                assert.strictEqual(fields[1].id, "email");
                assert.strictEqual(fields[2].id, "password");

                const submitButton = form.querySelector("button[type='submit']");
                assert.isNotNull(submitButton);
                assert.strictEqual(submitButton.textContent, "Sign Up");
            });
        });
    });
    describe("renderLoginView", function () {
        context("when rendering login view", function () {
            it("should render login form with correct fields and submit button", function () {
                const handleSubmit = function() {};
                usersViews.renderLoginView(contentHeader, content, handleSubmit);

                assert.strictEqual(contentHeader.textContent, "Login");
                const info = content.querySelector("p");
                assert.strictEqual(info.textContent, "Please login to access our website.");

                const form = content.querySelector("form");
                assert.isNotNull(form);

                const fields = form.querySelectorAll("input");
                assert.strictEqual(fields.length, 2);
                assert.strictEqual(fields[0].id, "email");
                assert.strictEqual(fields[1].id, "password");

                const submitButton = form.querySelector("button[type='submit']");
                assert.isNotNull(submitButton);
                assert.strictEqual(submitButton.textContent, "Login");
            });
        });
    });
    describe("rendersignUpAndLoginButtons", function () {
        context("when rendering sign up and login buttons", function () {
            it("should render sign up and login buttons with correct links", function () {
                const buttons = usersViews.signUpAndLoginButtons();
                const anchors = buttons.querySelectorAll("a");
                assert.strictEqual(anchors.length, 2);
                assert.strictEqual(anchors[0].textContent, "Sign Up");
                assert.strictEqual(anchors[0].getAttribute("href"), "#users/create");

                assert.strictEqual(anchors[1].textContent, "Login");
                assert.strictEqual(anchors[1].getAttribute("href"), "#users/login");
            
            });
        });
     });
     describe("renderlogoutButton", function () {
        context("when rendering logout button", function () {
            it("should render logout button with correct text and style", function () {
                const button = usersViews.logoutButton();
                assert.strictEqual(button.textContent, "Logout");
            });
        });
     });
});