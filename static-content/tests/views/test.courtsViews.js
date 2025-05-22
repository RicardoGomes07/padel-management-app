import courtsViews from "../../spa/handlers/views/courtsviews.js";
const assert = window.chai.assert

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
            const [clubLink, rentalsLink, byDateLink, hoursLink] = links;

            assert.strictEqual(links.length, 4);
            assert.strictEqual(clubLink.textContent, "Club");
            assert.strictEqual(clubLink.getAttribute("href"), "#clubs/10");

            assert.strictEqual(rentalsLink.textContent, "Court Rentals");
            assert.strictEqual(rentalsLink.getAttribute("href"), "#clubs/10/courts/5/rentals");

            assert.strictEqual(byDateLink.textContent, "by date");
            assert.strictEqual(byDateLink.getAttribute("href"), "#clubs/10/courts/5/rentals/search");

            assert.strictEqual(hoursLink.textContent, "Available Hours");
            assert.strictEqual(hoursLink.getAttribute("href"), "#clubs/10/courts/5/available_hours");
        });
    });

    describe('renderCourtsByClubView', function () {
        it("should render list of courts with correct links", function () {
            const courts = [{ crid: 1, name: "Court A" }, { crid: 2, name: "Court B" }];

            courtsViews.renderCourtsByClubView(contentHeader, content, courts, 10, 0, 5, true);

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

            courtsViews.renderCourtRentalsView(contentHeader, content, rentals, 3, 9, 0, 2, true);

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

    describe('renderCreateClubForm', function () {
        it("should render create court form and update hash on submit", function () {
            courtsViews.renderCreateCourtForm(contentHeader, content, 42);

            assert.strictEqual(contentHeader.textContent, "Create Court");

            const input = content.querySelector("#courtName");
            const form = content.querySelector("form");
            input.value = "New Court";

            const event = new Event("submit", {
                bubbles: true,
                cancelable: true
            });

            form.dispatchEvent(event);

            assert.strictEqual(window.location.hash, "#clubs/42/courts/create?name=New%20Court");

            const backLink = content.querySelector("a");
            assert.strictEqual(backLink.textContent, "Back");
            assert.strictEqual(backLink.getAttribute("href"), "#clubs/42");
        });
    });

    describe('renderCourtAvailableHoursView', function () {
        // TODO: Implement this test
    });

    describe('renderCalendarToSearchAvailableHours', function () {
        // TODO: Implement this test
    });

    describe('renderRentalAvailableFinalHours', function () {
        // TODO: Implement this test
    });

});
