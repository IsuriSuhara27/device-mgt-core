import React from "react";
import {Typography, Row, Col, Form, Icon, Input, Button, Checkbox,} from 'antd';
import styles from './Login.less';
import axios from 'axios';

const {Title} = Typography;

class Login extends React.Component {
    render() {
        return (
            <div className={styles.main}>
                <div className={styles.content}>
                    <Row>
                        <Col span={4} offset={10}>
                            <Row style={{marginBottom: 20}}>
                                <Col>
                                    <img className={styles.logo} src={require('../../public/images/logo.svg')}/>
                                </Col>
                            </Row>
                            <Title type="secondary" level={2}>Login</Title>
                            <WrappedNormalLoginForm/>

                        </Col>
                    </Row>
                    <Row>
                        <Col span={4} offset={10}>

                        </Col>
                    </Row>
                </div>
            </div>

        );
    }
}

class NormalLoginForm extends React.Component {
    handleSubmit = (e) => {
        e.preventDefault();
        this.props.form.validateFields((err, values) => {
            if (!err) {
                console.log('Received values of form: ', values);
                let data = "username="+values.username+"&password="+values.password+"&platform=publisher";
                axios.post('https://localhost:9443/api/application-mgt-handler/v1.0/login', data
                    ).then(res => {
                        console.log(res);
                        console.log(res.data);
                    })
            }

        });
    };

    render() {
        const {getFieldDecorator} = this.props.form;
        return (
            <Form onSubmit={this.handleSubmit} className="login-form">
                <Form.Item>
                    {getFieldDecorator('username', {
                        rules: [{required: true, message: 'Please input your username!'}],
                    })(
                        <Input style={{height: 32}} prefix={<Icon type="user" style={{color: 'rgba(0,0,0,.25)'}}/>}
                               placeholder="Username"/>
                    )}
                </Form.Item>
                <Form.Item>
                    {getFieldDecorator('password', {
                        rules: [{required: true, message: 'Please input your Password!'}],
                    })(
                        <Input style={{height: 32}} className={styles.input}
                               prefix={<Icon type="lock" style={{color: 'rgba(0,0,0,.25)'}}/>} type="password"
                               placeholder="Password"/>
                    )}
                </Form.Item>
                <Form.Item>
                    {getFieldDecorator('remember', {
                        valuePropName: 'checked',
                        initialValue: true,
                    })(
                        <Checkbox>Remember me....</Checkbox>
                    )}
                    <a className="login-form-forgot" href="">Forgot password</a>
                    <Button block type="primary" htmlType="submit" className="login-form-button">
                        Log in
                    </Button>
                </Form.Item>
            </Form>
        );
    }
}

const WrappedNormalLoginForm = Form.create({name: 'normal_login'})(NormalLoginForm);

export default Login;
