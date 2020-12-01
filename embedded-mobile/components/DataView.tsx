import * as React from 'react';
import {Text, View} from './Themed';
import {StyleSheet} from 'react-native';
import {Ionicons} from '@expo/vector-icons';
import {useEffect, useState} from "react";

const pics = ["ios-partly-sunny", "ios-locate", "ios-thermometer", "ios-expand", "ios-airplane"]

export default function DataView(props: any) {
    const [data, setData] = useState<Object>({})
    const [error, setError] = useState(false)

    const requestData = () => setTimeout(() => {
        fetch('http://192.168.0.100:8080/gps')
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
        <View style={styles.main}>
            {Object.entries(data).map(([key, value]) => (
                <View key={key} style={styles.dataBlock}>
                    <Text style={styles.title}>{key}:</Text>
                    <Text style={styles.value}>{value}</Text>
                    <Ionicons name={pics[Math.floor(Math.random() * pics.length)]} size={48} color="white"/>
                </View>
            ))}
        </View>
    );
}

const styles = StyleSheet.create({
    main: {
        width: '96%',
        alignSelf: 'center',
        backgroundColor: 'rgba(0, 0, 0, 0)',

    },
    dataBlock: {
        width: '100%',
        height: 90,
        borderRadius: 10,
        borderWidth: 2,
        borderColor: "white",
        backgroundColor: 'rgba(0, 0, 0, 0)',
        marginVertical: 5,
        flexDirection: 'row',

    },
    title: {
        color: "white",
        fontWeight: "bold",
        margin: 5,
    },
    value: {
        color: "white",
        margin: 5,
    }
});

// const pics2 = {
//     checksum: "checkcircle",
//     current_latitude: "ios-airplane",
//     current_longitude: "ios-airplane",
//     east_or_west: "ios-expand",
//     east_or_west_2:  "ios-expand",
//     north_or_south: "ios-contract",
//     speed_in_knots: "ios-speedometer",
//     time: "time-slot",
//     true_course: "arrow-top-right",
//     ut_date: "date-range",
//     validity: "emoticon-cool",
//     variation: "perm-identity"
// }
