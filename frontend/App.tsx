import React from "react";
import { StatusBar } from "expo-status-bar";
import { NavigationContainer, DarkTheme } from "@react-navigation/native";
import { RootNavigator } from "./src/navigation/RootNavigator";

export default function App() {
  return (
    <NavigationContainer theme={DarkTheme}>
      <RootNavigator />
      <StatusBar style="light" />
    </NavigationContainer>
  );
}
