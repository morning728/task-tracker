import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import "./i18n";
import App from './App';
import reportWebVitals from './reportWebVitals';

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();

// import React from "react";
// import ReactDOM from "react-dom";
// import { ThemeContextProvider} from "./ThemeContext" // Импорт провайдера контекста
// import MainPage from "./pages/MainPage/MainPage";

// ReactDOM.render(
//   <React.StrictMode>
//     <ThemeContextProvider>
//       <MainPage />
//     </ThemeContextProvider>
//   </React.StrictMode>,
//   document.getElementById("root")
// );


