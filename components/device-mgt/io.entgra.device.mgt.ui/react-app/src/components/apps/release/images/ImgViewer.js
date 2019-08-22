/*
 * Copyright (c) 2019, Entgra (pvt) Ltd. (http://entgra.io) All Rights Reserved.
 *
 * Entgra (pvt) Ltd. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import React, {Component} from 'react';
import RcViewer from 'rc-viewer';
import {Col} from "antd";

class ImgViewer extends Component {
    render() {
        const options = {
            title: false,
            toolbar: {
                zoomIn: 0,
                zoomOut: 0,
                oneToOne: 0,
                reset: 0,
                prev: 1,
                play: {
                    show: 0
                },
                next: 1,
                rotateLeft: 0,
                rotateRight: 0,
                flipHorizontal: 0,
                flipVertical: 0
            },
            rotatable: false,
            transition: false,
            movable : false
        };
        return (
            <div>
                <RcViewer options={options} ref='viewer'>
                    {this.props.images.map((screenshotUrl) => {
                        return (
                            <Col key={"col-" + screenshotUrl} lg={6} md={8} xs={8} className="release-screenshot">
                                <img key={screenshotUrl} src={screenshotUrl}/>
                            </Col>
                        )
                    })}
                </RcViewer>
            </div>
        );

    }
}

export default ImgViewer;