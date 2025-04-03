function getClubs(mainContent){
    fetch(API_BASE_URL + "clubs")
        .then(res => res.json())
        .then(clubs => {
            const div = document.createElement("div")
            const h1 = document.createElement("h1")
            const text = document.createTextNode("Clubs")
            h1.appendChild(text)
            div.appendChild(h1)
            clubs.forEach(club => {
                const p = document.createElement("p")
                const a = document.createElement("a")
                const aText = document.createTextNode("ClubDetails" + club.name);
                a.appendChild(aText)
                a.href="#clubs/" + club.cid
                p.appendChild(a)
                div.appendChild(p)
            })
            mainContent.replaceChildren(div)
        })
}

function getClub(mainContent, cid) {
    fetch(API_BASE_URL + "clubs/" + window.location.hash.split("/")[2]) //clubs[0] cid[1]
        .then(res => res.json())
        .then(club => {
            const ulClub = document.createElement("ul")
            const liName = document.createElement("li") 
            const textName = document.createTextNode("Club Name: " + club.name)    
            liName.appendChild(textName)

            const liOwner = document.createElement("li")
            const owner = document.createElement("a")
            const textOwner = document.createTextNode("Owner: " + club.owner.name);
            owner.appendChild(textOwner)
            owner.href="#users/" + club.owner.uid
            liOwner.appendChild(textOwner)

            const liBack = document.createElement("li")
            const a = document.createElement("a")
            const aText = document.createTextNode("Clubs")
            a.appendChild(aText)
            a.href="#clubs"
            liBack.appendChild(a)

            

            ulClub.appendChild(liName)
            ulClub.appendChild(liOwner)
            ulClub.appendChild(liBack)

            mainContent.replaceChildren(ulClub)
        })
}