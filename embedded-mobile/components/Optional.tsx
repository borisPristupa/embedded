import * as React from 'react';
import {ScrollView, StyleSheet} from 'react-native';
import {Text, View} from './Themed';
import {useEffect, useState} from "react";

export default function Optional() {
    const [data, setData] = useState({})
    const [error, setError] = useState(false)

    const requestData = () => setTimeout(() => {
        console.log('requestData')
        fetch('http://localhost:8080/gps')
            .then(response => response.json())
            .then(text => setData(text))
            .then(() => setError(false))
            .then(() => requestData())
            .catch(() => {
                setError(true)
                requestData()
            });

    }, 1000)

    useEffect(() => {
        requestData()
    }, [])

    return (
        <>
            {error && <Text style={styles.warning}>Data request error</Text>}
            <View style={styles.options}>
                <View style={styles.info_field}>
                    <ScrollView scrollEnabled={true}>
                        {Object.entries(data).map(([key, value]) => (
                            <Text style={styles.btn_text}>{key} = {value}</Text>
                        ))}
                    </ScrollView>
                </View>
            </View>
        </>
    );
}

// const createAlert = () => {
//     console.log('createAlert')
//     Alert.alert(
//         "🙁 Failed to request data",
//         "Please try again later",
//         [{text: "OK", onPress: () => console.log("OK Pressed")}],
//         {cancelable: false}
//     );
// }
const styles = StyleSheet.create({
    options: {
        flexDirection: 'row',
        width: '97%',
        height: 300,
        marginVertical: 5,
        alignSelf: 'center',
        borderRadius: 5,

    },
    btn_field: {
        alignSelf: "center",
        flex: 1,
        backgroundColor: "#fff",

    },
    info_field: {
        alignSelf: "center",
        flex: 1,
        backgroundColor: "green",

    },
    bnt: {
        backgroundColor: "green",
        alignSelf: "center",
        width: "90%",
    },
    btn_text: {
        color: "white",
    },
    warning: {
        width: 130,
        height: 30,
        marginHorizontal: 10,
        textAlign:"center",
        borderRadius: 4,
        color: "#EFA331",
        backgroundColor: "white",
    }
});
