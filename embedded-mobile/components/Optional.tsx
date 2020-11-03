import * as React from 'react';
import {Alert, ScrollView, StyleSheet} from 'react-native';
import { Button } from 'react-native-elements';
import { Text, View } from './Themed';
import {useState} from "react";

export default function Optional(props: any) {
    const [data, setData] = useState('')

    const onPress = () => {
        fetch('https://run.mocky.io/v3/c99b753a-52e8-4903-a6c0-f51e11d2e6b4')
            .then(response => {
                if (!response.ok) {
                    createTwoButtonAlert()
                    return ''
                }
                return response.text()
            })
            .then(text => setData(text))
            .catch(() => createTwoButtonAlert());
    }

    //onClickAction = () => {
    //     fetch('https://run.mocky.io/v3/c99b753a-52e8-4903-a6c0-f51e11d2e6b4')
    //       .then(resp => resp.json())
    //       .then(json => this.setState({count: JSON.stringify( json)}));
    //} Example  json-result for class

    return (
        <View style={styles.options}>
            <View style={styles.info_field}>
                <ScrollView scrollEnabled={true}>
                <Text style={styles.btn_text}>{data}</Text>
                </ScrollView>
            </View>
            <View style={styles.btn_field}>
                <Button title={props.btnTitle} buttonStyle={styles.bnt} titleStyle={styles.btn_text}
                    onPress={onPress}/>
            </View>
        </View>
    );
}

const createTwoButtonAlert = () =>
    Alert.alert(
        "🙁 Failed to request data",
        "Please try again later",
        [{ text: "OK", onPress: () => console.log("OK Pressed") }],
        { cancelable: false }
    );

const styles = StyleSheet.create({
    options: {
        flexDirection: 'row',
        width: '97%',
        height: 150,
        marginVertical: 5,
        alignSelf: 'center',
        borderRadius:5,

    },
    btn_field: {
        alignSelf: "center",
        flex:1,
        backgroundColor: "#fff",

    },
    info_field: {
        alignSelf: "center",
        flex: 1,
        backgroundColor: "green",

    },
    bnt: {
        backgroundColor: "green",
        alignSelf:"center",
        width: "90%",
    },
    btn_text: {
        color: "white",
    },
});
