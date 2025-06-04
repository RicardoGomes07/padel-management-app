import clubViews from "../../spa/handlers/views/clubsviews.js";
import {ELEMS_PER_PAGE} from "../../spa/handlers/views/pagination.js";
import {setAuthStatusContent, setUserInfo} from "../../spa/managers/userAuthenticationContext.js";

const assert = window.chai.assert

describe('ClubViews', function () {
    let contentHeader, content, authContent;

    const mockUser = { uid: 1, token: "mockToken"}

    beforeEach(function () {
        contentHeader = document.createElement('div');
        content = document.createElement('div');
        authContent = document.createElement('div');
        setAuthStatusContent(authContent)
    });

    describe('renderClubDetailsView', function () {
        it("should render club details with related links and hrefs when autenticated", function () {
            const club = {cid: 1, name: "Benfica", owner: {uid: 1, name: "Leonel"}};
            setUserInfo(mockUser)
            clubViews.renderClubDetailsView(contentHeader, content, club);

            assert.strictEqual(contentHeader.textContent, "Club Info");

            const links = content.querySelectorAll("a");
            const [ownerLink, courtsLink, createCourtLink, rentLink, allClubsLink] = links;

            assert.strictEqual(links.length, 5);
            assert.strictEqual(ownerLink.textContent, "Leonel");
            assert.strictEqual(ownerLink.getAttribute("href"), `#users/1`);

            assert.strictEqual(courtsLink.textContent, "Courts");
            assert.strictEqual(courtsLink.getAttribute("href"), `#clubs/1/courts?page=1`);

            assert.strictEqual(createCourtLink.textContent, "Create Court");
            assert.strictEqual(createCourtLink.getAttribute("href"), `#clubs/1/courts/create`);

            assert.strictEqual(rentLink.textContent, "Rent");
            assert.strictEqual(rentLink.getAttribute("href"), `#clubs/1/courts/rent`);

            assert.strictEqual(allClubsLink.textContent, "All Clubs");
            assert.strictEqual(allClubsLink.getAttribute("href"), "#clubs?page=1");
            setUserInfo(null);
        });

        it("should render club details with related links and hrefs when unauthenticated", function () {
            const club = {cid: 1, name: "Benfica", owner: {uid: 1, name: "Leonel"}};
            clubViews.renderClubDetailsView(contentHeader, content, club);

            assert.strictEqual(contentHeader.textContent, "Club Info");

            const links = content.querySelectorAll("a");
            const [ownerLink, courtsLink, allClubsLink] = links;

            assert.strictEqual(links.length, 3);
            assert.strictEqual(ownerLink.textContent, "Leonel");
            assert.strictEqual(ownerLink.getAttribute("href"), `#users/1`);

            assert.strictEqual(courtsLink.textContent, "Courts");
            assert.strictEqual(courtsLink.getAttribute("href"), `#clubs/1/courts?page=1`);

            assert.strictEqual(allClubsLink.textContent, "All Clubs");
            assert.strictEqual(allClubsLink.getAttribute("href"), "#clubs?page=1");
        });

    });

    describe('clubsList with valid list', function () {
        it("should render a list of clubs", function () {
            const clubs = [
                { cid: 1, name: "Benfica" },
                { cid: 2, name: "Porto" }
            ]

            content.replaceChildren(clubViews.clubsList(clubs))

            const items = content.querySelectorAll("a");
            assert.strictEqual(items.length, 2);

            assert.strictEqual(items[0].textContent, "Benfica");
            assert.strictEqual(items[0].getAttribute("href"), "#clubs/1");

            assert.strictEqual(items[1].textContent, "Porto");
            assert.strictEqual(items[1].getAttribute("href"), "#clubs/2");
        });
    });

    describe('clubList with empty list', function () {
        it("should render with 'No clubs found' if the list is empty", function () {
            const clubs = [];

            content.replaceChildren(clubViews.clubsList(clubs))

            assert.strictEqual(content.textContent, "No clubs found");
        });
    })

    describe('renderClubsView without name', function () {
        it("should render list of clubs with correct links, should not have create club when unauthenticated", function () {
            const clubs = [{cid: 1, name: "Benfica"}, {cid: 2, name: "Porto"}];

            clubViews.renderClubsView(contentHeader, content, clubs, 2, "", 1);

            assert.strictEqual(contentHeader.textContent, "Clubs");

            const links = content.querySelectorAll("a");
            const [searchBarLink, benficaLink, portoLink, currLink] = links;

            // 2 from clubsList, 1 from searchBar and 1 from pagination with elemsPerPage as 2
            assert.strictEqual(links.length, clubs.length + 1+ Math.ceil(clubs.length / ELEMS_PER_PAGE));

            assert.strictEqual(searchBarLink.textContent, "Search Clubs");
            assert.strictEqual(searchBarLink.getAttribute("href"), "#clubs?page=1");

            assert.strictEqual(benficaLink.textContent, "Benfica");
            assert.strictEqual(benficaLink.getAttribute("href"), "#clubs/1");

            assert.strictEqual(portoLink.textContent, "Porto");
            assert.strictEqual(portoLink.getAttribute("href"), "#clubs/2");

            assert.strictEqual(currLink.textContent, "Curr")
            assert.strictEqual(currLink.getAttribute("href"), "#clubs?page=1")
        });
    });

    describe('renderClubsView with name', function () {
        it("renderClubsView with name, should have create club when user is authenticated", function () {
            const clubs = [{cid: 1, name: "Benfica"}];
            setUserInfo(mockUser)

            clubViews.renderClubsView(contentHeader, content, clubs, 1, "Ben", 1);

            assert.strictEqual(contentHeader.textContent, "Clubs: Ben");

            const links = content.querySelectorAll("a");
            const [searchBarLink, benficaLink, currLink, createClubLink] = links;

            assert.strictEqual(links.length, clubs.length + 2 + Math.ceil(clubs.length / ELEMS_PER_PAGE));

            assert.strictEqual(searchBarLink.textContent, "Search Clubs");
            assert.strictEqual(searchBarLink.getAttribute("href"), "#clubs?page=1");

            assert.strictEqual(benficaLink.textContent, "Benfica");
            assert.strictEqual(benficaLink.getAttribute("href"), "#clubs/1");

            assert.strictEqual(createClubLink.textContent, "Create a Club")
            assert.strictEqual(createClubLink.getAttribute("href"), "#clubs/create")

            assert.strictEqual(currLink.textContent, "Curr")
            assert.strictEqual(currLink.getAttribute("href"), "#clubs?name=Ben&page=1")
            setUserInfo(null)
        });
    })

    describe('renderCreateClub', function () {
        it("should render create club form", function () {
            clubViews.renderCreateClubView(contentHeader, content, () => {});

            assert.strictEqual(contentHeader.textContent, "Create a Club");

            const form = content.querySelector("form");
            assert.ok(form, "Form should exist");

            const input = form.querySelector("#clubName");
            assert.ok(input, "Club name input should exist");
            assert.strictEqual(input.getAttribute("type"), "text");
            assert.strictEqual(input.getAttribute("required"), "");
            assert.strictEqual(input.getAttribute("id"), "clubName");

            const submitButton = form.querySelector("button[type='submit']");
            assert.ok(submitButton, "Submit button should exist");
            assert.strictEqual(submitButton.textContent, "Create Clubs");

            const backLink = content.querySelector("a");
            assert.ok(backLink, "Back link should exist");
            assert.strictEqual(backLink.textContent, "Back");
            assert.strictEqual(backLink.getAttribute("href"), "#clubs?page=1");
        });
    });
});