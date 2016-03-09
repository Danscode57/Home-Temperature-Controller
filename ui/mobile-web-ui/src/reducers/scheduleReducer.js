const initialState = {
    active: false,
    days: [
        {name: 'Mon', hours: []},
        {name: 'Tue', hours: []},
        {name: 'Wed', hours: []},
        {name: 'Thu', hours: []},
        {name: 'Fri', hours: []},
        {name: 'Sat', hours: []},
        {name: 'Sun', hours: []}
    ]
};

import { TOGGLE_SCHEDULE, DAY_SCHEDULE_UPDATE_TIME, DAY_SCHEDULE_UPDATE_TEMP,
    DAY_SCHEDULE_REMOVE_HOUR, DAY_SCHEDULE_ADD_HOUR, DAY_SCHEDULE_COPY, SCHEDULE_RECEIVE } from '../actions/actionTypes';

import R from 'ramda';

function timeComparator(a, b) {
    let aTime = new Date(Date.parse('1970-01-01 ' + a.time));
    let bTime = new Date(Date.parse('1970-01-01 ' + b.time));
    return aTime.getTime() - bTime.getTime();
}

function updateHourOfSchedule(state, field, day, index, value) {
    let dayState = state.days[day];
    let hour = dayState.hours[index];
    hour = R.assoc(field, value)(hour);
    let sortedHours = R.sort(timeComparator)(R.update(index, hour)(dayState.hours));
    dayState = R.assoc('hours')(sortedHours)(dayState);

    return R.assoc('days')(R.update(day)(dayState)(state.days))(state);
}

function removeFromSchedule(state, day, index) {
    let dayState = state.days[day];
    dayState.hours.splice(index, 1);
    return R.assoc('days')(R.update(day)(dayState)(state.days))(state);
}

function addHourSchedule(state, day) {
    let dayState = state.days[day];
    let newHour = {time: '00:01', temp: 18.5};

    if (dayState.hours.length > 0) {
        let lastHour = R.last(dayState.hours);
        newHour = {time: R.clone(lastHour.time), temp: 18.5};
    }
    dayState.hours.push(newHour);

    return R.assoc('days')(R.update(day)(dayState)(state.days))(state);
}

function copyPreviousDay(state, day) {
    let previous = day - 1;
    if (previous < 0) return state;
    let previousDay = R.clone(state.days[previous].hours);
    let newDay = R.assoc('hours')(previousDay)(state.days[day]);
    return R.assoc('days')(R.update(day)(newDay)(state.days))(state);
}

export default function scheduleReducer(state = initialState, action) {
    switch (action.type) {
        case TOGGLE_SCHEDULE:
            let currentState = state.active;
            return R.assoc('active', !currentState)(state);
        case DAY_SCHEDULE_UPDATE_TIME:
            let newTime = action.newTime.getHours() + ':' + action.newTime.getMinutes();
            return updateHourOfSchedule(state, 'time', action.day, action.index, newTime);
        case DAY_SCHEDULE_UPDATE_TEMP:
            return updateHourOfSchedule(state, 'temp', action.day, action.index, action.newTemp);
        case DAY_SCHEDULE_REMOVE_HOUR:
            return removeFromSchedule(state, action.day, action.index);
        case DAY_SCHEDULE_ADD_HOUR:
            return addHourSchedule(state, action.day);
        case DAY_SCHEDULE_COPY:
            return copyPreviousDay(state, action.day);
        case SCHEDULE_RECEIVE:
            return action.schedule;
        default:
            return state;
    }
}

