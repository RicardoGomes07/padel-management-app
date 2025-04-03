function getCourtsByClub(mainContent, cid){
    fetch(API_BASE_URL + "courts/club/" + cid + "?skip=" + skip + "&limit=" + limit)
        .then(res => res.json())
        .then(courts => {
            div(
                h1("Courts"),
                courts.map(court => ul(
                    li(court.name),
                    li(a("Info", "#courts/" + court.cid)),
                )),
            )
            const nextLink = a("Next", "#courts/club/" + cid + "?skip=" + (skip + limit) + "&limit=" + limit);
            const prevLink = a("Prev", "#courts/club/" + cid + "?skip=" + (skip - limit) + "&limit=" + limit);
            
            if (skip === 0) prevLink.style.display = "none";
            if (skip + limit >= courts.length) nextLink.style.display = "none";
            mainContent.replaceChildren(div, nextLink, prevLink)
        }
    )       
}

function getCourt(mainContent, crid) {
    fetch(API_BASE_URL + "courts/" + crid)
        .then(res => res.json())
        .then(court => {
            div(
                h2("Court"),
                ul(
                    li(court.name),
                    li(a("Club", "#clubs/" + court.club.cid)),
                )
            )
            mainContent.replaceChildren(div)
        }
    )       
}