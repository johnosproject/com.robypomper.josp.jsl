
function pad(s) {
    return (s < 10) ? '0' + s : s;
}

// 9:00
export const getHourString = date => {
    if (date == null)
        date = new Date();
    if (typeof date === 'number')
        date = new Date(date);

    return pad(date.getHours()) + ':00';
}

// 21/04/2021
export const getDateString = date => {
    if (date == null)
        date = new Date();
    if (typeof date === 'number')
        date = new Date(date);

    return [pad(date.getDate()), pad(date.getMonth()+1), date.getFullYear()].join('/');
}

// 09:19:02
export const getTimeString = date => {
    if (date == null)
        date = new Date();
    if (typeof date === 'number')
        date = new Date(date);

    return [pad(date.getHours()), pad(date.getMinutes()), pad(date.getSeconds())].join(':');
}

// 21/04/2021 09:19:02
export const getDateTimeString = date => {
    if (date == null)
        date = new Date();
    if (typeof date === 'number')
        date = new Date(date);

    return getDateString(date) + ' ' + getTimeString(date);
}

// 20210421-091902
export const getDateTimeCode = date => {
    if (date == null)
        date = new Date();
    if (typeof date === 'number')
        date = new Date(date);

    return [pad(date.getDate()), pad(date.getMonth()+1), date.getFullYear()].join('') + '-'
        + [pad(date.getHours()), pad(date.getMinutes()), pad(date.getSeconds())].join('');
}

// 09:19:02 21/04/2021
export const getTimeDateString = date => {
    if (date == null)
        date = new Date();
    if (typeof date === 'number')
        date = new Date(date);

    return getTimeString(date) + ' '
        + getDateString(date);
}

// Deprecated because duplicate of getDateString()
// 21/04/2021
export const getDayString = date => {
    if (date == null)
        date = new Date();
    if (typeof date === 'number')
        date = new Date(date);

    return [pad(date.getDate()), pad(date.getMonth()+1), date.getFullYear()].join('/');
}
