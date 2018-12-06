import React from "react";
import {
    Form, Icon, Input, Button, Checkbox,
} from 'antd';

import "antd/dist/antd.css";
import "./shellBox.css"
const FormItem = Form.Item;

class NormalLoginForm extends React.Component {
    handleSubmit = (e) => {
        console.log("props",this.props)
        e.preventDefault();
        console.log("data",this.props.form)
        this.props.form.validateFields((err, values) => {
            if (!err) {
                this.props.onClick(values)
            }
        });
    };

    render() {
        const { getFieldDecorator } = this.props.form;
        return (
            <Form onSubmit={this.handleSubmit} className="login-form">
                <FormItem>
                    {getFieldDecorator('ip', {
                        rules: [{ required: true, message: 'ip不能为空' }],
                    })(
                        <Input prefix={<Icon type="global" style={{ color: 'rgba(0,0,0,.25)' }} />} placeholder="ip" />
                    )}
                </FormItem>
                <FormItem>
                    {getFieldDecorator('username', {
                        rules: [{ required: true, message: 'username不能为空' }],
                    })(
                        <Input prefix={<Icon type="user" style={{ color: 'rgba(0,0,0,.25)' }} />} placeholder="username" />
                    )}
                </FormItem>
                <FormItem>
                    {getFieldDecorator('password', {
                        rules: [{ required: true, message: 'password不能为空' }],
                    })(
                        <Input prefix={<Icon type="lock" style={{ color: 'rgba(0,0,0,.25)' }} />} type="password" placeholder="password" />
                    )}
                </FormItem>
                <FormItem>
                    {getFieldDecorator("remember", {
                        valuePropName: "checked",
                        initialValue: true
                    })(<Checkbox>Remember me</Checkbox>)}
                    <Button
                        type="primary"
                        htmlType="submit"
                        className="login-form-button"
                    >
                        连接
                    </Button>
                </FormItem>
            </Form>
        );
    }
}

const ShellInput = Form.create()(NormalLoginForm);

export default ShellInput