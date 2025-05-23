import courtsViews from "../../spa/handlers/views/courtsviews.js";
import pagination  from "../../spa/handlers/views/pagination.js";
const assert = window.chai.assert
const { DEFAULT_VALUE_SKIP, DEFAULT_VALUE_LIMIT} = pagination


describe('CourtsViews', function () {
    let contentHeader, content;

    beforeEach(function () {
        contentHeader = document.createElement('div');
        content = document.createElement('div');
    });

    describe('renderCourtDetailsView', function () {
        it("should render court details with related links and hrefs", function () {
            const court = { name: "Court X" };

            courtsViews.renderCourtDetailsView(contentHeader, content, court, 10, 5);

            assert.strictEqual(contentHeader.textContent, "Court Info");

            const links = content.querySelectorAll("a");
            const [clubLink, rentalsLink, byDateLink, hoursLink, rentCourt] = links;

            assert.strictEqual(links.length, 5);
            assert.strictEqual(clubLink.textContent, "Club");
            assert.strictEqual(clubLink.getAttribute("href"), "#clubs/10");

            assert.strictEqual(rentalsLink.textContent, "Court Rentals");
            assert.strictEqual(rentalsLink.getAttribute("href"), `#clubs/10/courts/5/rentals?page=1`);

            assert.strictEqual(byDateLink.textContent, "by date");
            assert.strictEqual(byDateLink.getAttribute("href"), "#clubs/10/courts/5/rentals/search");

            assert.strictEqual(hoursLink.textContent, "Available Hours");
            assert.strictEqual(hoursLink.getAttribute("href"), "#clubs/10/courts/5/available_hours");

            assert.strictEqual(rentCourt.textContent, "Rent Court");
            assert.strictEqual(rentCourt.getAttribute("href"), "#clubs/10/courts/5/rentals/create");
        });
    });

    describe('renderCourtsByClubView', function () {
        it("should render list of courts with correct links", function () {
            const courts = [{ crid: 1, name: "Court A" }, { crid: 2, name: "Court B" }];

            courtsViews.renderCourtsByClubView(contentHeader, content, courts, 2, 10, 1);

            assert.strictEqual(contentHeader.textContent, "Courts");

            const links = content.querySelectorAll("ul li a");

            assert.strictEqual(links.length, 2);
            assert.strictEqual(links[0].textContent, "Court A");
            assert.strictEqual(links[0].getAttribute("href"), "#clubs/10/courts/1");

            assert.strictEqual(links[1].textContent, "Court B");
            assert.strictEqual(links[1].getAttribute("href"), "#clubs/10/courts/2");
        });
    });

    describe('renderCourtRentalsView', function () {
        it("should render court rentals with links and correct hrefs", function () {
            const rentals = [
                { rid: 1, date: "2024-05-01", initialHour: "10", finalHour: "12" },
                { rid: 2, date: "2024-05-02", initialHour: "14", finalHour: "16" }
            ];

            courtsViews.renderCourtRentalsView(contentHeader, content, rentals, 2, 3, 9, 2);

            assert.strictEqual(contentHeader.textContent, "Rentals");

            const links = content.querySelectorAll("ul li a");

            assert.strictEqual(links.length, 2);

            assert.strictEqual(links[0].textContent.includes("2024-05-01"), true);
            assert.strictEqual(links[0].getAttribute("href"), "#clubs/3/courts/9/rentals/1");

            assert.strictEqual(links[1].textContent.includes("2024-05-02"), true);
            assert.strictEqual(links[1].getAttribute("href"), "#clubs/3/courts/9/rentals/2");

            const backLink = content.querySelector("div a");
            assert.strictEqual(backLink.textContent, "Back");
            assert.strictEqual(backLink.getAttribute("href"), "#clubs/3/courts/9");
        });
    });

    describe("renderCreateCourtForm", function () {
        it("should render the create court form with correct elements", function () {
            courtsViews.renderCreateCourtForm(contentHeader, content, 42,() => {});

            assert.strictEqual(contentHeader.textContent, "Create Court");

            const form = content.querySelector("form");
            assert.ok(form, "Form should exist");

            const input = form.querySelector("#courtName");
            assert.ok(input, "Court name input should exist");
            assert.strictEqual(input.getAttribute("type"), "text");
            assert.strictEqual(input.getAttribute("required"), "");
            assert.strictEqual(input.getAttribute("placeholder"), "Enter Court Name");

            const submitButton = form.querySelector("button[type='submit']");
            assert.ok(submitButton, "Submit button should exist");
            assert.strictEqual(submitButton.textContent, "Create Court");

            const backLink = content.querySelector("a");
            assert.ok(backLink, "Back link should exist");
            assert.strictEqual(backLink.textContent, "Back");
            assert.strictEqual(backLink.getAttribute("href"), "#clubs/42");
        });
    });

    describe("renderCourtAvailableHoursView", function () {
        it("should render header, back link, and available hours", function () {
            const availableHours = [
                { start: 10, end: 11 },
                { start: 12, end: 13 }
            ];
            const cid = 1;
            const crid = 2;
            const selectedDate = "2025-05-22";

            courtsViews.renderCourtAvailableHoursView(contentHeader, content, availableHours, cid, crid, selectedDate);

            assert.strictEqual(contentHeader.textContent, "Available Hours for 2025-05-22");

            const backLink = content.querySelector("a");
            assert.ok(backLink, "Back link should exist");
            assert.strictEqual(backLink.textContent, "Back");
            assert.strictEqual(backLink.getAttribute("href"), "#clubs/1/courts/2/available_hours");

            const listItems = content.querySelectorAll("ul li");
            assert.strictEqual(listItems.length, 2, "Should display 2 time slots");
            assert.strictEqual(listItems[0].textContent, "10 to 11");
            assert.strictEqual(listItems[1].textContent, "12 to 13");
        });
    });

    describe("renderCalendarToSearchAvailableHours", function () {
        it("should render header, back link, and form with date input", function () {
            const cid = 1;
            const crid = 2;

            courtsViews.renderCalendarToSearchAvailableHours(contentHeader, content, cid, crid, () => {});

            // Header check
            assert.strictEqual(contentHeader.textContent, "Search Available Hours");

            // Backlink check
            const backLink = content.querySelector("a");
            assert.ok(backLink, "Back link should exist");
            assert.strictEqual(backLink.textContent, "Back");
            assert.strictEqual(backLink.getAttribute("href"), "#clubs/1/courts/2");

            // Form check
            const form = content.querySelector("form");
            assert.ok(form, "Form should exist");

            // Date input check
            const dateInput = form.querySelector("#date");
            assert.ok(dateInput, "Date input should exist");
            assert.strictEqual(dateInput.getAttribute("type"), "date");
            assert.ok(dateInput.required, "Date input should be required");

            // Submit button check
            const submitBtn = form.querySelector("button[type='submit']");
            assert.ok(submitBtn, "Submit button should exist");
            assert.strictEqual(submitBtn.textContent, "Get Available Hours");
        });
    });

    describe("renderSearchForCourtsByDateAndTimeSlot", function () {
        it("should render header, back link, and form with date and time inputs", function () {
            const cid = 5;

            courtsViews.renderSearchForCourtsByDateAndTimeSlot(contentHeader, content, cid, () => {});

            // Header check
            assert.strictEqual(
                contentHeader.textContent,
                "Select a date and a time to search for courts availability"
            );

            // Backlink check
            const backLink = content.querySelector("a");
            assert.ok(backLink, "Back link should exist");
            assert.strictEqual(backLink.textContent, "Back");
            assert.strictEqual(backLink.getAttribute("href"), "#clubs/5");

            // Form check
            const form = content.querySelector("form");
            assert.ok(form, "Form should exist");

            // Inputs check
            const dateInput = form.querySelector("#date");
            assert.ok(dateInput, "Date input should exist");
            assert.strictEqual(dateInput.getAttribute("type"), "date");
            assert.ok(dateInput.required, "Date input should be required");

            const startHourInput = form.querySelector("#startHour");
            assert.ok(startHourInput, "Start hour select should exist");
            assert.strictEqual(startHourInput.tagName.toLowerCase(), "select");
            assert.ok(startHourInput.required, "Start hour select should be required");

            const endHourInput = form.querySelector("#endHour");
            assert.ok(endHourInput, "End hour select should exist");
            assert.strictEqual(endHourInput.tagName.toLowerCase(), "select");
            assert.ok(endHourInput.required, "End hour select should be required");
        });
    });

    describe("renderAvailableCourtsToRent", function () {

        it("should render header and list of available courts with correct links", function () {
            const availableCourts = [
                { cid: 1, crid: 10, name: "Court A" },
                { cid: 2, crid: 20, name: "Court B" }
            ];
            const date = "2024-05-23";
            const startHour = "10";
            const endHour = "12";

            courtsViews.renderAvailableCourtsToRent(contentHeader, content, availableCourts, date, startHour, endHour);

            // Check header text
            assert.strictEqual(contentHeader.textContent, "Available Courts");

            // Check that content contains a list
            const ulElement = content.querySelector("ul");
            assert.ok(ulElement, "There should be a ul element");

            // Check the number of list items
            const listItems = ulElement.querySelectorAll("li");
            assert.strictEqual(listItems.length, availableCourts.length);

            // Check each list item has the correct link text and href
            listItems.forEach((li, i) => {
                const a = li.querySelector("a");
                assert.ok(a, "Each list item should contain a link");
                assert.strictEqual(a.textContent, availableCourts[i].name);

                // The href should match createRentalUri with the correct parameters
                const expectedHref = `#clubs/${availableCourts[i].cid}/courts/${availableCourts[i].crid}/rentals/create?date=${date}&start=${startHour}&end=${endHour}`;
                assert.strictEqual(a.getAttribute("href"), expectedHref);
            });
        });
    });

});
