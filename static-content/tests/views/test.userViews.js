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
                assert.strictEqual(rentalLink.getAttribute("href"), "#users/1/rentals");
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
                const skip = 0;
                const limit = 5;
                const hasNext = true;

                usersViews.renderUserRentalsView(contentHeader, content, rentals, username, uid, skip, limit, hasNext);
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
                const skip = 0;
                const limit = 5;
                const hasNext = false;

                usersViews.renderUserRentalsView(contentHeader, content, rentals, username, uid, skip, limit, hasNext);
                assert.strictEqual(contentHeader.textContent, "Rentals of Ric");

                const message = content.querySelector("p");
                assert.strictEqual(message.textContent, "No rentals found");

                const backLink = content.querySelector("div a");
                assert.strictEqual(backLink.textContent, "Back");
                assert.strictEqual(backLink.getAttribute("href"), "#users/1");
            });
        });
    });
});