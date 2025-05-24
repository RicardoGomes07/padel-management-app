import  pagination  from "../../spa/handlers/views/pagination.js";
const assert = window.chai.assert
describe('createPaginationLinks', function () {

    it("should render correct pagination links for page 5 of 10", function () {
        const baseLink = "/test?page=5";
        const count = 100; // → 10 pages
        const page = 5;

        const container = pagination.createPaginationLinks(baseLink, count, page);
        const links = container.querySelectorAll("a");
        const labels = Array.from(links).map(link => link.textContent);

        assert.deepEqual(labels, [
            "1", "2", "3", "Prev", "Curr", "Next", "7", "8", "9", "..."
        ]);

        // Check the current page link
        const currLink = Array.from(links).find(link => link.textContent === "Curr");
        assert.strictEqual(currLink.href.includes("page=5"), true);
    });

    it("should show only 'Curr' if there's only one page", function () {
        const baseLink = "/test?page=1";
        const count = 5; // Only 1 page
        const page = 1;

        const container = pagination.createPaginationLinks(baseLink, count, page);
        const links = container.querySelectorAll("a");
        const labels = Array.from(links).map(link => link.textContent);

        assert.deepEqual(labels, ["Curr", "Next", "3"]);
    });
});

