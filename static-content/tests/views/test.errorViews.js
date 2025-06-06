import errorViews from "../../spa/handlers/views/errorsview.js";
const assert = window.chai.assert

const DEFAULT_ERROR_TITLE = "Unknown Error";
const DEFAULT_ERROR_MESSAGE = "Something went wrong";

describe("ErrorViews", function () {
    let errorContent;

    beforeEach(function () {
        errorContent = document.createElement("div");
        document.body.appendChild(errorContent);
    });

    afterEach(function () {
        document.body.removeChild(errorContent);
    });

    describe("errorView", function () {
        it("should render error view with default error details", function () {
            const view = errorViews.errorView();
            errorContent.appendChild(view);

            const paragraphs = errorContent.querySelectorAll("p");
            assert.strictEqual(paragraphs.length, 2);
            assert.strictEqual(paragraphs[0].textContent, DEFAULT_ERROR_TITLE + ":");
            assert.strictEqual(paragraphs[1].textContent, DEFAULT_ERROR_MESSAGE);
        });

        it("should render error view with custom error details", function () {
            const customError = {
                title: "Custom Error",
                message: "This is a custom error message",
                description: "Detailed error description"
            };

            const view = errorViews.errorView(customError);
            errorContent.appendChild(view);

            const paragraphs = errorContent.querySelectorAll("p");
            assert.strictEqual(paragraphs.length, 2);
            assert.strictEqual(paragraphs[0].textContent, customError.title + ":");
            assert.strictEqual(paragraphs[1].textContent, customError.description);
        });
    });
});
