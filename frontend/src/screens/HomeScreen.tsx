import React from "react";
import { StyleSheet, Text, View } from "react-native";
import type { NativeStackScreenProps } from "@react-navigation/native-stack";
import type { RootStackParamList } from "../types/nav";
import { Screen } from "../components/Screen";
import { Button } from "../components/Button";
import { app } from "../constants/app";
import { colors } from "../theme/colors";

type Props = NativeStackScreenProps<RootStackParamList, "Home">;

export function HomeScreen({ navigation }: Props) {
  return (
    <Screen>
      <View style={styles.container}>
        <Text style={styles.title}>{app.name}</Text>
        <Text style={styles.subtitle}>{app.tagline}</Text>

        <Button title="혼자 놀기" onPress={() => navigation.navigate("Solo")} />
        <Button
          title="같이 놀기"
          variant="secondary"
          onPress={() => navigation.navigate("Rooms")}
        />
      </View>
    </Screen>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 24,
    justifyContent: "center",
    gap: 12,
  },
  title: {
    color: colors.text,
    fontSize: 34,
    fontWeight: "700",
    letterSpacing: 0.2,
  },
  subtitle: {
    color: colors.muted,
    fontSize: 14,
    marginBottom: 24,
  },
});
