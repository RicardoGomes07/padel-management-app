import rentalsViews from "../../spa/handlers/views/rentalsviews.js";
const assert = window.chai.assert

describe("rentalsViews", function () {
    let contentHeader, content;

    const rental = {
        rid: "1",
        court: { crid: 1, name: "Court1", cid: 1 },
        renter: { uid: 1, name: "John Doe", email: "john@doe.com" },
        date: "2023-10-01",
        initialHour: 10,
        finalHour: 12,
    };

    beforeEach(function () {
        contentHeader = document.createElement("div");
        content = document.createElement("div");
    });

    describe("renderRentalDetailsView", function () {
        it("should set header text", function () {
            rentalsViews.renderRentalDetailsView(contentHeader, content, rental);
            assert.strictEqual(contentHeader.innerHTML, "Rental Info");
        });

        it("should display rental info", function () {
            rentalsViews.renderRentalDetailsView(contentHeader, content, rental);

            assert.include(content.innerHTML, "Court: ");
            assert.include(content.innerHTML, "Court1");
            assert.include(content.innerHTML, "Renter: ");
            assert.include(content.innerHTML, "John Doe");
            assert.include(content.innerHTML, "Date: 2023-10-01");
            assert.include(content.innerHTML, "TimeSlot: 10h - 12h");
            assert.include(content.innerHTML, "Update Rental");
            assert.include(content.innerHTML, "Delete Rental");
        });

        it("should render correct hrefs for links", function () {
            rentalsViews.renderRentalDetailsView(contentHeader, content, rental);

            const links = content.querySelectorAll("a");
            assert.strictEqual(links.length, 4, "There should be exactly 4 links");

            assert.strictEqual(links[0].textContent, "Court1");
            assert.strictEqual(links[0].getAttribute("href"), "#clubs/1/courts/1");

            assert.strictEqual(links[1].textContent, "John Doe");
            assert.strictEqual(links[1].getAttribute("href"), "#users/1");

            assert.strictEqual(links[2].textContent, "Update Rental");
            assert.strictEqual(links[2].getAttribute("href"), "#clubs/1/courts/1/rentals/1/update");

            assert.strictEqual(links[3].textContent, "Delete Rental");
            assert.strictEqual(links[3].getAttribute("href"), "#clubs/1/courts/1/rentals/1/delete");
        });
    });

    describe("renderCalendarToSearchRentals", function () {
        const cid = 1;
        const crid = 1;
        const handleSubmit = () => {}; 


        it("should render header and form", function () {
            rentalsViews.renderCalendarToSearchRentals(contentHeader, content, cid, crid, handleSubmit);

            assert.strictEqual(contentHeader.innerHTML, "Search Rentals");

            const form = content.querySelector("form");
            assert.ok(form, "Form should exist");

            const input = form.querySelector("input#date");

            assert.ok(input, "Date input should exist");
            assert.strictEqual(input.getAttribute("type"), "date");
            assert.ok(input.hasAttribute("required"), "Date input should be required");

            const submit = form.querySelector("button[type='submit']");
            assert.ok(submit, "Submit button should exist");
            assert.strictEqual(submit.textContent, "Search Rentals");

            const backLink = content.querySelector("a[href='#clubs/1/courts/1']");
            assert.ok(backLink, "Back link should exist");
            assert.strictEqual(backLink.textContent, "Back");
        });

    });

    describe("renderUpdateRentalView", function () {
        it("should render the update form with pre-filled values", function () {
            rentalsViews.renderUpdateRentalView(contentHeader, content, rental, () => {});

            // Check header
            assert.strictEqual(contentHeader.innerHTML, "Update Rental");

            // Check Back link
            const backLink = content.querySelector("a");
            assert.ok(backLink, "Back link should exist");
            assert.strictEqual(backLink.textContent, "Back");
            assert.strictEqual(
                backLink.getAttribute("href"),
                "#clubs/1/courts/1/rentals/1"
            );

            // Check form
            const form = content.querySelector("form");
            assert.ok(form, "Form should exist");

            const dateInput = form.querySelector("#date");
            assert.ok(dateInput, "Date input should exist");
            assert.strictEqual(dateInput.value, "2023-10-01");

            const startHour = form.querySelector("#startHour");
            assert.ok(startHour, "Start hour input should exist");
            assert.strictEqual(startHour.value, "10");

            const endHour = form.querySelector("#endHour");
            assert.ok(endHour, "End hour input should exist");
            assert.strictEqual(endHour.value, "12");

            const submitBtn = form.querySelector("button[type='submit']");
            assert.ok(submitBtn, "Submit button should exist");
            assert.strictEqual(submitBtn.textContent, "Update Rental");
        });
    });

    describe("renderRentalCreationForm", function () {
        const cid = 1;
        const crid = 2;

        const rentalInfo = {
            date: "2024-05-20",
            startHour: 10,
            endHour: 12,
        };

        beforeEach(function () {
            rentalsViews.renderRentalCreationForm(
                contentHeader,
                content,
                cid,
                crid,
                rentalInfo,
                () => {}
            );
        });

        it("should render header and form with pre-filled values", function () {
            assert.strictEqual(contentHeader.innerHTML, "Create Rental");

            const form = content.querySelector("form");
            assert.ok(form, "Form should exist");

            assert.strictEqual(form.querySelector("#date").value, "2024-05-20");
            assert.strictEqual(form.querySelector("#startHour").value, "10");
            assert.strictEqual(form.querySelector("#endHour").value, "12");

            const submitBtn = form.querySelector("button[type='submit']");
            assert.ok(submitBtn, "Submit button should exist");
            assert.strictEqual(submitBtn.textContent, "Create Rental");
        });

        it("should render a Back link with correct href", function () {
            const backLink = content.querySelector("a");
            assert.ok(backLink, "Back link should exist");
            assert.strictEqual(backLink.textContent, "Back");
            assert.strictEqual(backLink.getAttribute("href"), "#clubs/1/courts/2");
        });
    });
});