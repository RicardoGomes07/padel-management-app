// Just a simple function to parse the hour from a string
// This function receives the hour string is in the format "HH:MM" how it is shown in the UI
// and returns the hour as a number (integer)
// For example, "14:00" will return 14 
function parseHourFromString(hourString) {
    const [hour, minute] = hourString.split(":").map(Number)
    return hour
}


function isValidDate(dateString) {
    const date = new Date(dateString)
    return !isNaN(date.getTime())
}


const auxiliaryFuns = {
    isValidDate,
    parseHourFromString
}

export default auxiliaryFuns