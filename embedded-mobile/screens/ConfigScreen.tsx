import * as React from 'react';
import {StyleSheet} from 'react-native';

import {Text, View} from '../components/Themed';

export default function ConfigScreen() {
    return (
        <View style={styles.container}>
            <Text style={styles.title}>Hello</Text>
        </View>
    );
}

const styles = StyleSheet.create({
    container: {
        alignItems: 'center',
        justifyContent: 'center',
        marginTop: 30,
        width: "100%",
        flex: 1,
        backgroundColor: "#8bc34a"
    },
    title: {
        fontSize: 20,
        marginVertical: 3,
        fontWeight: 'bold',
        alignSelf: "center",
        color: "#005b01",
    }
});
