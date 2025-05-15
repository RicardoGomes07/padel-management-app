import { before, describe, it } from "node:test";
import usersViews from "../../spa/handlers/views/usersviews.js";

describe('UsersViews', function () {
    let contentHeader, content;
    
    before(function () {
        contentHeader = document.createElement('div');
        content = document.createElement('div');
      });

    describe('renderUserDetailsView', function () {
        context('when rendering user details', function () {
            it('should set header text', function () {
                const user = {uid: "1", name: "Ric", email: "riczao@gmail.com" };
                usersViews.renderUserDetailsView(contentHeader, content, user);
                contentHeader.innerHTML.should.be.equal("User Info");
            });

            it('should display user info', function () {
                const user = {uid: "1", name: "Ric", email: "riczao@gmail.com" };
                usersViews.renderUserDetailsView(contentHeader, content, user);

                content.innerHTML.should.contain("Name: Ric");
                content.innerHTML.should.contain("Email: riczao@gmail.com")
            });
        });
    });
});