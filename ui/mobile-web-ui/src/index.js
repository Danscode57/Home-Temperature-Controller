import 'core-js/fn/object/assign';
import React from 'react';
import ReactDOM from 'react-dom';

import Home from './components/Home';
import App from './components/App';
import Schedule from './components/Schedule';

import injectTapEventPlugin from 'react-tap-event-plugin';
import { Router, Route, hashHistory, IndexRedirect } from 'react-router';

import { createStore, applyMiddleware, combineReducers } from 'redux';
import { Provider } from 'react-redux';
import createLogger from 'redux-logger';
import thunkMiddleware from 'redux-thunk'


import scheduleReducer from './reducers/scheduleReducer';
import syncReducer from './reducers/syncReducer';


let logger = createLogger();

let reducers = combineReducers({
    schedule: scheduleReducer,
    sync: syncReducer
});

let store = createStore(reducers, applyMiddleware(logger, thunkMiddleware));

injectTapEventPlugin();

ReactDOM.render((
    <Provider store={store}>
        <Router history={hashHistory}>
            <Route path="/" component={App}>
                <IndexRedirect to="home"/>
                <Route path="home" component={Home}/>
                <Route path="schedule" component={Schedule}/>

            </Route>
        </Router>
    </Provider>), document.getElementById('app'));

