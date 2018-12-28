import * as React from 'react';
import '../../App.css';
import { Button, Table } from "antd";
import 'antd/dist/antd.css';
import { setNavType } from '../../rpc-redux/actions'
import { withRouter } from 'react-router-dom';
import { connect, ConnectedComponentClass } from 'react-redux';
import {serviceDetail} from '../../rpc-redux/actions'
interface ITableType {
    service: string,
    group: string,
    appName: string
}
class IndexSearch extends React.Component<any, any> {
    public state = {
    }
    constructor(props: any) {
        super(props);
        window['AddpContext']['IndexSearch'] = {};
    }
    public setState(state: any) {
        super.setState(state, () => window['AddpContext']['IndexSearch'] = this.state);
    }
    public render() {
        this.props.dispatch(setNavType('index_search'));
        console.log("IndexSearch start...");
        return (
            <div className='conetntDiv'>
                <h4>查找到的RPC服务10条数据</h4>
                <hr />
                <Table<ITableType> dataSource={this.props.redux.data.serviceList} pagination={{ pageSize: 50 }} scroll={{ y: 240 }} >
                    <Table.Column<ITableType> key="service" title="服务ID" dataIndex="service" width="30%" />
                    <Table.Column<ITableType> key="group" title="分组" dataIndex="group" width="15%" />
                    <Table.Column<ITableType> key="appName" title="应用名" dataIndex="appName" width="15%" />
                    <Table.Column key="操作" title="操作" render={this.createOperationColumn} width="20%" />
                </Table>
            </div >
        );
    };
    private createOperationColumn = (rowData: any) => {
        return <div>
            <Button type="primary" onClick={() => {
                this.props.history.push({ pathname: "/detail"});
                this.props.dispatch(serviceDetail(rowData.key));
            }}>详情</Button>
            <Button type="primary" onClick={() => this.props.history.push({ pathname: "/serviceTest", state: { 'sign': rowData.key } })}>测试</Button>
        </div>;
    }
}
export default withRouter((connect(
    (state: any) => {
        return {
            redux: state.index
        }
    },
    (dispatch) => ({
        dispatch: (func: any) => func(dispatch)
    }),
    null,
    { withRef: false }
)(IndexSearch) as ConnectedComponentClass<typeof IndexSearch, any>));