import * as types from '../actions/actionTypes';
import R from 'ramda';

const initialState = {
    fetchingSchedule: false,
    savingSchedule: false
};


export default function syncReducer(state = initialState, action){
    switch(action.type){
        case types.SCHEDULE_REQUEST:
            return R.assoc('fetchingSchedule', true)(state);
        case types.SCHEDULE_RECEIVE:
            return R.assoc('fetchingSchedule', false)(state);
        default:
            return state;
    }

}