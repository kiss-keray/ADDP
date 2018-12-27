import * as React from 'react';
import './App.css';
import { Button, Table, message } from "antd";
import Fetch from './Fetch';
import 'antd/dist/antd.css';
import {withRouter} from 'react-router-dom';
interface ITableType {
    service: string,
    group: string,
    appName: string
}
class IndexSearch extends React.Component<any,any> {
    public state = {
        serviceData: [{
            service: 'com.nix.jingxun.addp.rpc.producer.test.Hello',
            group: 'RPC',
            appName: 'app1',
            key: 'com.nix.jingxun.addp.rpc.producer.test.Hello-app1-RPC-1.0.0'
        }]
    }
    constructor(props:any) {
        super(props);
        window['AddpContext']['IndexSearch'] = {};
    }
    public setState(state: any) {
        super.setState(state,() => window['AddpContext']['IndexSearch'] = this.state);
      }
    // 测试获取服务详情
    public serviceDetail = (sign: string) => {
        Fetch(`/ops/detail/?sign=${sign}`, {
            method: 'GET'
        }).then((detail: any) => {
            console.log('data = ', detail);
        }).catch(error => {
            console.log("error", error);
            message.error('获取服务详情失败！！！');
        });
    }
    public render() {
        window['navFun']('index_search');
        return (
            <div className='conetntDiv'>
                <h4>查找到的RPC服务10条数据</h4>
                <hr />
                <Table<ITableType> dataSource={this.state.serviceData} pagination={{ pageSize: 50 }} scroll={{ y: 240 }} >
                    <Table.Column<ITableType> key="service" title="服务ID" dataIndex="service" width="30%" />
                    <Table.Column<ITableType> key="group" title="分组" dataIndex="group" width="15%" />
                    <Table.Column<ITableType> key="appName" title="应用名" dataIndex="appName" width="15%" />
                    <Table.Column key="操作" title="操作" render={this.createOperationColumn} width="20%" />
                </Table>
            </div >);
    };

    private createOperationColumn = (rowData: any) => {
        return <div>
            <Button type="primary" onClick={() => this.props.history.push({ pathname: "/detail", state: {'sign':rowData.key } })}>详情</Button>
            <Button type="primary" onClick={() => this.serviceDetail(rowData.key)}>测试</Button>
        </div>;
    }
}
export default withRouter(IndexSearch);