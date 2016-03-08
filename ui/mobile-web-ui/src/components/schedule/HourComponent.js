require('normalize.css');
require('styles/App.css');

import R from 'ramda';
import React, { PropTypes, Component } from 'react';
import TimePicker from 'material-ui/lib/time-picker/time-picker';
import Slider from 'material-ui/lib/slider';

import FloatingActionButton from 'material-ui/lib/floating-action-button';
import ContentRemove from 'material-ui/lib/svg-icons/content/remove';

class HourComponent extends Component {

    constructor(props) {
        super(props);
        this.state = {dragStarted: true, temp: props.hour.temp};
    }

    handleDragStop(updateTempAction, day, index) {
        if (this.state.dragStarted) {
            this.setState({dragStarted: false});
            let newValue = this.refs.tempSlider.getValue();
            updateTempAction(day, index, newValue);
        }
    }

    handleOnChange(updateTempAction, day, index, newValue) {
        if (!this.state.dragStarted) {
            updateTempAction(day, index, newValue);
        } else {
            this.setState(R.assoc('temp', newValue)(this.state))
        }
    }

    render() {
        let { day ,hour, actions, index } = this.props;
        let temp = hour.temp;
        let time = hour.time;
        let defaultTime = new Date(Date.parse('1979-10-10 ' + time));


        return (
            <div>
                <div className="scheduleHourContainer">
                    <div className="scheduleHourItem">
                        <TimePicker hintText="Time" textFieldStyle={{width: 70}} defaultTime={defaultTime}
                                    onChange={ (e,t)=> actions.updateTime(day, index, t)}/>
                    </div>
                    <div className="scheduleHourItem">
                        <input type="text" value={this.state.temp} style={{width: 40}} readOnly={true}/>&deg;C
                    </div>
                    <div className="scheduleHourItem">
                        <FloatingActionButton mini={true} onClick={ () => actions.removeHourSchedule(day, index)}>
                            <ContentRemove />
                        </FloatingActionButton>
                    </div>
                </div>

                <Slider step={0.5} defaultValue={temp} min={15} max={23} ref="tempSlider"
                        onDragStart={() => this.setState(R.assoc('dragStarted', true)(this.state))}
                        onDragStop={ (e) => this.handleDragStop(actions.updateTemp, day, index)}
                        onChange={(e,v) => this.handleOnChange(actions.updateTemp, day, index, v)}/>

            </div>
        );
    }
}

HourComponent.propTypes = {
    hour: PropTypes.object.isRequired,
    actions: PropTypes.object.isRequired,
    index: PropTypes.number.isRequired,
    day: PropTypes.number.isRequired
};

export default HourComponent;