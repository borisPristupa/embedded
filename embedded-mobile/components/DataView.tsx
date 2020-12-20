import * as React from 'react';
import {Text, View} from './Themed';
import {StyleSheet} from 'react-native';
import {Ionicons} from '@expo/vector-icons';
import {useEffect, useState} from "react";
import ModalSelector from "react-native-modal-selector";

const pics = ["ios-partly-sunny", "ios-locate", "ios-thermometer", "ios-expand", "ios-airplane"]
const ports = [
    {key: '1', label: '1'},
    {key: '2', label: '2'},
    {key: '3', label: '3'},
    {key: '4', label: '4'},
    {key: '5', label: '5'},
    {key: '6', label: '6'},
]

export interface ObjectOf<T> {
    [key: string]: T
}

export default function DataView() {
    const [data, setData] = useState<ObjectOf<string>>({})
    const [portNum, setPortNum] = useState('1')

    const [error, setError] = useState(false)
    const [requestNum, setRequestNum] = useState(1)

    const fetchData = (url: string) => {
        fetch(url)
            .then(response => response.json())
            .then(text => setData(text))
            .then(() => setError(false))
            .then(() => setRequestNum(requestNum + 1))
            .catch(() => {
                setError(true)
                setRequestNum(requestNum + 1)
                setData({})
            });
    }

    useEffect(() => {
        const url = `http://192.168.0.40:8080/data?port=com${portNum}`
        const timer = setTimeout(() => fetchData(url), 1000)
        return () => clearTimeout(timer)
    }, [requestNum])

    const onChangePort = (option: any) => {
        setPortNum(() => option.key)
        setRequestNum(0)
    }

    return (
        <View style={styles.main}>
            <Text>
                <Text>Port:</Text>
                <ModalSelector
                    data={ports}
                    initValue={portNum}
                    animationType="slide"
                    selectTextStyle={styles.text}
                    // selectStyle={styles.selectModel}
                    touchableStyle={styles.touch}
                    backdropPressToClose={true}
                    cancelStyle={{display: 'none'}}
                    onChange={onChangePort}
                />
            </Text>
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
    },
    text: {
        color: 'white',
    },
    touch: {
        marginLeft: 5
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
