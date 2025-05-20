import errorViews from "../../spa/handlers/views/errorsview.js";
const assert = window.chai.assert

describe("ErrorViews", function () {
    let contentHeader, content;
    
    beforeEach(function () {
        contentHeader = document.createElement('div');
        content = document.createElement('div');
    });

    describe("errorView", function () {
        it("should render error view with default error details", function () {
            const backLocation = "#home";

            errorViews.errorView(contentHeader, content, backLocation);
            assert.strictEqual(contentHeader.textContent, "Unknown Error");

            const backLink = content.querySelector("a");
            assert.strictEqual(backLink.textContent, "Back");
            assert.strictEqual(backLink.getAttribute("href"), "#home");

            const errorDescription = content.querySelector("p");
            assert.strictEqual(errorDescription.textContent, "Something went wrong");

        });
    });
});