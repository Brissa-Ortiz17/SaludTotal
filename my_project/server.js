const express = require('express');
const bodyParser = require('body-parser');
const mysql = require('mysql');
const moment = require('moment'); // Asegúrate de instalar la biblioteca moment.js

const app = express();
app.use(bodyParser.json());

const db = mysql.createConnection({
    host: '127.0.0.1',
    user: 'root',
    password: '', // Reemplaza 'password' con tu contraseña de MySQL
    database: 'salud_movil',
    port: 3305
});

db.connect((err) => {
    if (err) {
        console.error('Error connecting to the database:', err);
        return;
    }
    console.log('Connected to the database');
});

// Endpoint para autenticar al usuario
app.post('/authenticate', (req, res) => {
    const { usuario, contrasena } = req.body;
    console.log(`Intento de autenticación para usuario: ${usuario}`); // Log para verificar los datos de entrada

    const sql = 'SELECT id_usuario, nombre, apellido FROM Usuarios WHERE usuario = ? AND contraseña = ?';
    db.query(sql, [usuario, contrasena], (err, results) => {
        if (err) {
            console.error('Error fetching user:', err);
            res.status(500).send('Error fetching user');
            return;
        }
        console.log('Resultados de la consulta:', results); // Log para verificar los resultados de la consulta
        if (results.length > 0) {
            res.json({ 
                id_usuario: results[0].id_usuario,
                nombre: results[0].nombre,
                apellido: results[0].apellido
            });
        } else {
            res.status(401).send('Unauthorized');
        }
    });
});

// Endpoint para registrar un nuevo usuario
app.post('/register', (req, res) => {
    const { nombre, apellido, fecha_nacimiento, email, contrasena, telefono, direccion, usuario, tipo_usuario } = req.body;
    const sql = 'INSERT INTO Usuarios (nombre, apellido, fecha_nacimiento, email, contraseña, telefono, direccion, usuario, tipo_usuario) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)';
    db.query(sql, [nombre, apellido, fecha_nacimiento, email, contrasena, telefono, direccion, usuario, tipo_usuario], (err, result) => {
        if (err) {
            console.error('Error inserting user:', err);
            res.status(500).send('Error inserting user');
            return;
        }
        res.status(200).send('User registered successfully');
    });
});

// Endpoint para obtener la lista de usuarios con nombre y apellido
app.get('/usuarios', (req, res) => {
    const sql = 'SELECT id_usuario, CONCAT(nombre, " ", apellido) AS nombre_completo FROM Usuarios';
    db.query(sql, (err, results) => {
        if (err) {
            console.error('Error fetching usuarios:', err);
            res.status(500).send('Error fetching usuarios');
            return;
        }
        res.json(results);
    });
});

// Endpoint para obtener la lista de doctores
app.get('/doctores', (req, res) => {
    db.query('SELECT id_doctor, nombre FROM Doctores', (err, results) => {
        if (err) {
            console.error('Error fetching doctores:', err);
            res.status(500).send('Error fetching doctores');
            return;
        }
        res.json(results);
    });
});

// Endpoint para obtener la lista de especialidades
app.get('/especialidades', (req, res) => {
    db.query('SELECT id_especialidad, nombre_especialidad FROM Especialidades', (err, results) => {
        if (err) {
            console.error('Error fetching especialidades:', err);
            res.status(500).send('Error fetching especialidades');
            return;
        }
        res.json(results);
    });
});

// Endpoint para crear una cita
app.post('/createAppointment', (req, res) => {
    const { id_usuario, id_doctor, fecha_cita, hora_cita, id_especialidad, ubicacion, nota } = req.body;
    const sql = 'INSERT INTO Citas (id_usuario, id_doctor, fecha_cita, hora_cita, id_especialidad, ubicacion, nota) VALUES (?, ?, ?, ?, ?, ?, ?)';
    db.query(sql, [id_usuario, id_doctor, fecha_cita, hora_cita, id_especialidad, ubicacion, nota], (err, result) => {
        if (err) {
            console.error('Error inserting appointment:', err);
            res.status(500).send('Error inserting appointment');
            return;
        }
        res.send('Appointment created successfully');
    });
});

// Endpoint para consultar citas
app.post('/consultarCitas', (req, res) => {
    console.log('Cuerpo de la solicitud:', req.body);

    const { id_usuario, id_doctor, id_especialidad } = req.body;

    let query = `SELECT * FROM Citas WHERE id_usuario = ?`;
    let queryParams = [id_usuario];

    if (id_doctor !== undefined && id_doctor !== -1) {
        query += ` AND id_doctor = ?`;
        queryParams.push(id_doctor);
    }
    if (id_especialidad !== undefined && id_especialidad !== -1) {
        query += ` AND id_especialidad = ?`;
        queryParams.push(id_especialidad);
    }

    console.log('Consulta SQL:', query);
    console.log('Parámetros de consulta:', queryParams);

    db.query(query, queryParams, (err, results) => {
        if (err) {
            console.error('Error en la consulta:', err);
            res.status(500).send('Error en la consulta');
            return;
        }
        console.log('Resultados de la consulta:', results);
        res.json(results);
    });
});

// Endpoint para consultar citas con filtros adicionales
app.post('/consultarCitasConFiltro', (req, res) => {
    const { id_usuario, filtro, id_filtro } = req.body;

    let query = 'SELECT * FROM Citas WHERE id_usuario = ? AND fecha_cita >= CURDATE()';
    const params = [id_usuario];

    if (filtro && id_filtro !== undefined) {
        switch (filtro) {
            case 'doctor':
                query += ' AND id_doctor = ?';
                params.push(id_filtro);
                break;
            case 'especialidad':
                query += ' AND id_especialidad = ?';
                params.push(id_filtro);
                break;
            default:
                res.status(400).send('Filtro no válido');
                return;
        }
    }

    db.query(query, params, (err, results) => {
        if (err) {
            console.error('Error en la consulta:', err);
            res.status(500).send('Error en la consulta');
            return;
        }
        res.json(results);
    });
});

// Endpoint para crear un recordatorio y múltiples notificaciones
app.post('/createRecordatorio', (req, res) => {
    const { id_usuario, nombre, duracion, hora_inicio, frecuencia, nota, fecha_inicio, fecha_fin } = req.body;

    const sqlRecordatorio = 'INSERT INTO Recordatorios (id_usuario, nombre, duracion, hora_inicio, frecuencia, nota, fecha_inicio, fecha_fin) VALUES (?, ?, ?, ?, ?, ?, ?, ?)';
    
    db.query(sqlRecordatorio, [id_usuario, nombre, duracion, hora_inicio, frecuencia, nota, fecha_inicio, fecha_fin], (err, result) => {
        if (err) {
            console.error('Error inserting recordatorio:', err);
            res.status(500).send('Error inserting recordatorio');
            return;
        }

        const idRecordatorio = result.insertId;
        const notificaciones = [];
        const startDate = moment(fecha_inicio);
        const startTime = moment(hora_inicio, "HH:mm");

        for (let day = 0; day < duracion; day++) {
            for (let hour = 0; hour < 24; hour += frecuencia) {
                const notificationTime = moment(startDate).add(day, 'days').add(hour, 'hours').set({
                    hour: startTime.hour(),
                    minute: startTime.minute(),
                    second: 0
                });
                notificaciones.push([idRecordatorio, notificationTime.format('YYYY-MM-DD'), notificationTime.format('HH:mm:ss'), nota]);
            }
        }

        const sqlNotificacion = 'INSERT INTO Notificaciones (id_recordatorio, fecha, hora, nota) VALUES ?';

        db.query(sqlNotificacion, [notificaciones], (err, result) => {
            if (err) {
                console.error('Error inserting notificacion:', err);
                res.status(500).send('Error inserting notificacion');
                return;
            }

            res.send('Recordatorio y notificaciones creados exitosamente');
        });
    });
});

// Endpoint para consultar recordatorios
app.post('/consultarRecordatorios', (req, res) => {
    const { id_usuario, filtro, id_filtro } = req.body;

    let query = `SELECT 
                    nombre, 
                    duracion, 
                    hora_inicio, 
                    frecuencia, 
                    nota, 
                    DATE(fecha_inicio) as fecha_inicio, 
                    DATE(fecha_fin) as fecha_fin 
                FROM Recordatorios 
                WHERE id_usuario = ? AND fecha_fin >= CURDATE()`;
    const params = [id_usuario];

    if (filtro && id_filtro !== undefined) {
        switch (filtro) {
            case 'doctor':
                query += ' AND id_doctor = ?';
                params.push(id_filtro);
                break;
            case 'especialidad':
                query += ' AND id_especialidad = ?';
                params.push(id_filtro);
                break;
            default:
                res.status(400).send('Filtro no válido');
                return;
        }
    }

    db.query(query, params, (err, results) => {
        if (err) {
            console.error('Error en la consulta:', err);
            res.status(500).send('Error en la consulta');
            return;
        }
        res.json(results);
    });
});

// Endpoint para consultar recordatorios anteriores
app.post('/consultarRecordatoriosAnteriores', (req, res) => {
    const { id_usuario } = req.body;

    const query = `SELECT 
                    nombre, 
                    duracion, 
                    hora_inicio, 
                    frecuencia, 
                    nota, 
                    DATE(fecha_inicio) as fecha_inicio, 
                    DATE(fecha_fin) as fecha_fin 
                FROM Recordatorios 
                WHERE id_usuario = ? AND fecha_fin < CURDATE()`;

    db.query(query, [id_usuario], (err, results) => {
        if (err) {
            console.error('Error en la consulta:', err);
            res.status(500).send('Error en la consulta');
            return;
        }
        res.json(results);
    });
});
// Endpoint para obtener la cita por fecha y usuario
app.get('/getCitaByDate', (req, res) => {
    const { id_usuario, fecha_cita } = req.query;

    if (!id_usuario || !fecha_cita) {
        res.status(400).send('id_usuario y fecha_cita son requeridos');
        return;
    }

    const sql = `
        SELECT 
            id_cita, 
            id_usuario, 
            id_doctor, 
            DATE_FORMAT(fecha_cita, '%Y-%m-%d') AS fecha_cita, 
            hora_cita, 
            id_especialidad, 
            ubicacion, 
            nota 
        FROM Citas 
        WHERE id_usuario = ? AND DATE(fecha_cita) = ?
    `;

    db.query(sql, [id_usuario, fecha_cita], (err, results) => {
        if (err) {
            console.error('Error fetching cita by date:', err);
            res.status(500).send('Error fetching cita by date');
            return;
        }

        if (results.length > 0) {
            res.json(results[0]);
        } else {
            res.status(404).send('No hay citas para esa fecha');
        }
    });
});

// Endpoint para obtener las citas de un usuario
app.get('/getCitasByUsuario', (req, res) => {
    const id_usuario = req.query.id_usuario;

    if (!id_usuario) {
        res.status(400).send('id_usuario es requerido');
        return;
    }

    const sql = `
        SELECT 
            id_cita, 
            id_usuario, 
            id_doctor, 
            DATE_FORMAT(fecha_cita, '%Y-%m-%d') AS fecha_cita, 
            hora_cita, 
            id_especialidad, 
            ubicacion, 
            nota 
        FROM Citas 
        WHERE id_usuario = ?
    `;

    db.query(sql, [id_usuario], (err, results) => {
        if (err) {
            console.error('Error fetching citas:', err);
            res.status(500).send('Error fetching citas');
            return;
        }

        res.json(results);
    });
});


//Endpoint para encontrar proxima cita
app.get('/getProximaCita', (req, res) => {
    const id_usuario = req.query.id_usuario;
    
    const sql = `SELECT * FROM Citas 
                WHERE id_usuario = ? 
                AND fecha_cita >= CURDATE() 
                ORDER BY fecha_cita ASC, hora_cita ASC 
                LIMIT 1`;

    db.query(sql, [id_usuario], (err, results) => {
        if (err) {
            console.error('Error al obtener la próxima cita:', err);
            res.status(500).send('Error al obtener la próxima cita');
            return;
        }

        if (results.length > 0) {
            res.json(results[0]);
        } else {
            res.status(404).send('No hay citas próximas');
        }
    });
});

const port = 3000;
const host = '192.168.100.144'; // Usa la IP de mi máquina en lugar de '0.0.0.0'

// const host = '192.168.59.26';  // IP datos moviles

app.listen(port, host, () => {
    console.log(`Server running on http://${host}:${port}`);
});
