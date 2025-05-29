import Html from "../../dsl/htmlfuns.js";
import pagination from "./pagination.js";
import uriManager from "../../managers/uriManager.js";
import errorViews from "./errorsview.js";

const { errorView } = errorViews
const { createPaginationLinks } = pagination
const { div, a, ul, li, p, formElement, span, label, input, logoutButton } = Html
const { getCourtDetailsUri, listClubCourtsUri, getClubDetailsUri, listCourtRentalsUri, searchCourtRentalsUri,
    getCourtAvailableHoursUri, getRentalDetailsUri, createRentalUri, getAvailableHoursByDateAndRangeUri } = uriManager

function renderCourtsByClubView(contentHeader, content, courts, count, cid, page) {
    const currHeader = contentHeader.textContent
    const header = "Courts" 

    const courtList = courts.length > 0
        ? ul(
            ...courts.map(court =>
                li(a(court.name, getCourtDetailsUri(cid, court.crid)))
            )
        )
        : p("No courts found")

    const back = a("Back", `#clubs/${cid}`)

    const navigation = createPaginationLinks(listClubCourtsUri(cid, page), count, page)

    if (currHeader !== header) contentHeader.replaceChildren(header, logoutButton())
    content.replaceChildren(courtList, navigation, back)
}

function renderCourtDetailsView(contentHeader, content, courtResponse, cid, crid, page) {
    const header = "Court Info"
    const info =
        ul(
            li(`Name : ${courtResponse.name}`),
            li(a("Club", getClubDetailsUri(cid))),
            li(
                span(
                    a("Court Rentals", listCourtRentalsUri(cid, crid, page)),
                    a("by date", searchCourtRentalsUri(cid, crid)),
                )
            ),
            li(a("Available Hours", getCourtAvailableHoursUri(cid, crid))),
            li(a("Rent Court", createRentalUri(cid, crid))),
        )

    contentHeader.replaceChildren(header, logoutButton())
    content.replaceChildren(info)
}

function renderCourtRentalsView(contentHeader, content, rentals, count, cid, crid, page) {
    const currHeader = contentHeader.textContent
    const header = "Rentals"

    const backLink = div(a("Back", getCourtDetailsUri(cid, crid)))

    const rentalList = rentals.length > 0
        ? ul(
            ...rentals.map(rental =>
                li(
                    a(`${rental.date.toString()} ${rental.initialHour} to ${rental.finalHour === 24 ? "23:59" : rental.finalHour } `,
                        getRentalDetailsUri(cid, crid, rental.rid)
                    )
                )
            )
        )
        : p("No rentals found")

    const navigation = createPaginationLinks(listCourtRentalsUri(cid, crid, page), count, page)

    if (currHeader !== header) contentHeader.replaceChildren(header, logoutButton())
    content.replaceChildren(backLink, rentalList, navigation)
}

function renderCreateCourtForm(contentHeader, content, cid, handleSubmit) {
    const header = "Create Court"

    const fields = [
        { id: "courtName", label: "Name: ", type: "text", required: true, placeholder: "Enter Court Name" },
    ]

    const backLink = div(a("Back", getClubDetailsUri(cid)))
    const children = li(
        formElement(fields, handleSubmit, {
            className: "form",
            submitText: "Create Court"
        })
    )

    contentHeader.replaceChildren(header, logoutButton())
    content.replaceChildren(children, backLink)
}

function renderCourtAvailableHoursView(contentHeader, content, availableHours, cid, crid, selectedDate, selectedRangeId) {
    const backLink = div(a("← Back", getCourtAvailableHoursUri(cid, crid)))

    let createRentalAnchor = null
    let selectedHoursDiv = null

    if (selectedRangeId) {
        const [startStr, endStr] = selectedRangeId.split("-")
        const start = Number(startStr)
        const end = Number(endStr)

        if (!rangeIsValid(availableHours, start, end)) {
            errorView(contentHeader, content, getCourtAvailableHoursUri(cid, crid), {
                title: "Invalid Range",
                description: "The selected range is not valid or does not exist."
            })
            return
        }

        const checkboxes = []

        function onCheckboxChange() {
            const allCheckboxes = selectedHoursDiv.querySelectorAll("input[type='checkbox']")
            const checkedBoxes = Array.from(allCheckboxes).filter(cb => cb.checked)

            if (checkedBoxes.length >= 2) {
                allCheckboxes.forEach(cb => {
                    if (!cb.checked) cb.disabled = true
                })
            } else {
                allCheckboxes.forEach(cb => {
                    cb.disabled = false
                })
            }

            if (createRentalAnchor) {
                createRentalAnchor.remove()
                createRentalAnchor = null
            }

            if (checkedBoxes.length === 2) {
                const selectedHours = checkedBoxes.map(cb => cb.id.split("-").pop())
                createRentalAnchor = a(
                    "Create Rental",
                    createRentalUri(cid, crid, selectedDate, selectedHours[0], selectedHours[1] )
                )
                createRentalAnchor.style.cssText = `
                    display: inline-block;
                    margin-top: 0.5em;
                    font-weight: bold;
                    color: #007bff;
                    cursor: pointer;
                    text-decoration: underline;
                `
                selectedHoursDiv.appendChild(createRentalAnchor)
            }
        }

        for (let hour = start; hour <= end; hour++) {
            const checkboxId = `chk-${selectedRangeId}-${hour}`
            const hourLabel = formatHour(hour)
            const checkbox = input(checkboxId, "checkbox", "", "", false, () => onCheckboxChange())
            const checkboxLabel = label(checkboxId, hourLabel)
            const container = div(checkbox, checkboxLabel)
            container.style.margin = "0.25em 0"
            checkboxes.push(container)
        }

        selectedHoursDiv = div(...checkboxes)
        selectedHoursDiv.style.cssText = `
            border: 1px solid #ccc;
            padding: 0.5em;
            margin-bottom: 1em;
        `
    }

    const intervalsList = ul(
        ...availableHours.map(range => {
            const rangeId = `${range.start}-${range.end}`
            const isSelected = rangeId === selectedRangeId

            const linkText = `${formatHour(range.start)} to ${formatHour(range.end)}`
            const href = getAvailableHoursByDateAndRangeUri(cid, crid, selectedDate, rangeId)

            const anchor = a(linkText, href)
            if (isSelected) {
                anchor.style.fontWeight = "bold"
                anchor.style.textDecoration = "underline"
            }

            return li(anchor)
        })
    )

    const headerText = `Available Hours for ${selectedDate}`
    contentHeader.replaceChildren(headerText, logoutButton())
    if (selectedHoursDiv) {
        content.replaceChildren(backLink, selectedHoursDiv, intervalsList)
    } else {
        content.replaceChildren(backLink, intervalsList)
    }
}

function rangeIsValid(availableHours, start, end) {
    if (typeof start !== "number" || typeof end !== "number") return false;
    if (isNaN(start) || isNaN(end)) return false;
    if (start >= end) return false;

    return availableHours.some(range => range.start === start && range.end === end);
}

// formata 8 → "08:00", 13 → "13:00"
function formatHour(hour) {
    if(hour === 24) {
        return "23:59"
    } else {
        return `${hour.toString().padStart(2, "0")}:00`
    }
}


function renderCalendarToSearchAvailableHours(contentHeader, content, cid, crid, handleSubmit) {
    const header = "Search Available Hours"

    const fields = [
        { id: "date", label: "Select Date", type: "date", required: true },
    ]

    const children = div(
        a("Back", getCourtDetailsUri(cid, crid)),
        formElement(fields, handleSubmit, {
            className: "form",
            submitText: "Get Available Hours"
        })
    )

    contentHeader.replaceChildren(header, logoutButton())
    content.replaceChildren(children)
}

function renderSearchForCourtsByDateAndTimeSlot(contentHeader, content, cid, submitHandler){
    const backLink = div(a("Back", getClubDetailsUri(cid)))

    const fields = [
        { id: "date", label: "Select Date", type: "date", required: true },
        {id: "startHour", label: "Start Hour", type: "hour", required: true},
        { id: "endHour", label: "End Hour", type: "hour", required: true},
    ]

    const form = formElement(fields, submitHandler)
    contentHeader.replaceChildren("Select a date and a time to search for courts availability", logoutButton())
    content.replaceChildren(backLink,form)
}

function renderAvailableCourtsToRent(contentHeader, content, availableCourts, date, startHour, endHour){
    const header = "Available Courts"
    
    const courts = ul(
        ...availableCourts.map(court =>
            li(a(court.name, createRentalUri(court.cid, court.crid, date, startHour, endHour)))
        )
    )
    contentHeader.replaceChildren(header, logoutButton())
    content.replaceChildren(courts)
}

const courtsViews = {
    renderCourtsByClubView,
    renderCourtDetailsView,
    renderCourtRentalsView,
    renderCreateCourtForm,
    renderCourtAvailableHoursView,
    renderCalendarToSearchAvailableHours,
    renderSearchForCourtsByDateAndTimeSlot,
    renderAvailableCourtsToRent,
}

export default courtsViews