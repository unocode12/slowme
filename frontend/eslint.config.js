const { FlatCompat } = require("@eslint/eslintrc");

// eslint config itself runs in Node; define baseDirectory without relying on globals that lint rules might flag.
const baseDirectory = process.cwd();
const compat = new FlatCompat({ baseDirectory });

module.exports = [
  ...compat.extends("expo"),
  {
    rules: {
      "no-console": "off",
    },
  },
];
