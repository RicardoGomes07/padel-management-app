// Auxiliary function to split the available hours into hourly slots
// [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11]
// Will appear as:
// 00:00, 01:00, 02:00, 03:00, 04:00, 05:00, 06:00, 07:00, 08:00, 09:00, 10:00, 11:00
function splitIntoHourlySlots(start, end) {
    const result = [];
    for (let hour = start; hour <= end; hour++) {
        const startStr = `${hour.toString().padStart(2, "0")}:00`;
        result.push({ start: startStr });
    }
    return result;
}

// Just a simple function to parse the hour from a string
// This function receives the hour string is in the format "HH:MM" how it is shown in the UI
// and returns the hour as a number (integer)
// For example, "14:00" will return 14 
function parseHourFromString(hourString) {
    const [hour, minute] = hourString.split(":").map(Number);
    return hour; 
}

export { splitIntoHourlySlots, parseHourFromString };