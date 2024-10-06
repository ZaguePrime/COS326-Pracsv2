function insertPatients(dbName, colName, patientCount) {
    const ailments = ["Cold", "Flu", "Injury", "Allergy", "Headache"];
    const doctors = ["Dr. Green", "Dr. Smith", "Dr. Brown", "Dr. Adams"];
    
    for (let i = 0; i < patientCount; i++) {
        const patient = {
            name: "Patient " + (i + 1),
            age: Math.floor(Math.random() * 60) + 18, // Random age between 18 and 78
            ailment: ailments[Math.floor(Math.random() * ailments.length)],
            doctor: doctors[Math.floor(Math.random() * doctors.length)],
            admitted: Math.random() > 0.5, // Randomly admitted or not
            appointments: [
                {
                    date: "2024-09-01",
                    reason: "Initial checkup"
                },
                {
                    date: "2024-09-15",
                    reason: "Follow-up"
                }
            ]
        };
        db.getSiblingDB(dbName).getCollection(colName).insertOne(patient);
    }
    print(patientCount + " patients inserted into " + dbName + "." + colName);
}

function findAdmittedPatients(dbName, colName) {
    const admittedPatients = db.getSiblingDB(dbName).getCollection(colName).find({ admitted: true }).toArray();
    print("Admitted Patients:");
    admittedPatients.forEach(patient => printjson(patient));
}

function updatePatientAdmission(dbName, colName, patientName, status) {
    const result = db.getSiblingDB(dbName).getCollection(colName).updateOne(
        { name: patientName },
        { $set: { admitted: status } }
    );
    if (result.matchedCount > 0) {
        print("Patient " + patientName + " admission status updated to " + status);
    } else {
        print("No patient found with the name " + patientName);
    }
}

function removeDischargedPatients(dbName, colName) {
    const result = db.getSiblingDB(dbName).getCollection(colName).deleteMany({ admitted: false });
    print(result.deletedCount + " discharged patients removed from " + dbName + "." + colName);
}

function doctorStats(dbName, colName) {
    db.getSiblingDB(dbName).getCollection(colName).aggregate([
        {
            $match: { admitted: true }  // Only consider admitted patients
        },
        {
            $group: {
                _id: "$doctor",          // Group by doctor's name
                patientCount: { $sum: 1 } // Count the number of patients
            }
        },
        {
            $sort: { _id: 1 }            // Sort by doctor's name in ascending order
        }
    ]).forEach(doc => printjson(doc));
}

function doctorPatientList(dbName, colName, doctorName) {
    db.getSiblingDB(dbName).getCollection(colName).find(
        { doctor: doctorName, admitted: true },   // Match patients assigned to the doctor and currently admitted
        { name: 1, _id: 0 }                      // Only return the patient's name
    ).forEach(patient => printjson(patient));
}

function activeDoctorsMR(dbName, colName) {
    const result = db.getSiblingDB(dbName).getCollection(colName).aggregate([
        {
            $match: { admitted: true }           // Only consider admitted patients
        },
        {
            $group: {
                _id: "$doctor",                  // Group by doctor's name
                patientCount: { $sum: 1 }        // Count the number of patients
            }
        }
    ]).toArray();
    
    db.getSiblingDB(dbName).getCollection("DoctorActivity").insertMany(result);
    print("Doctor activity data inserted into DoctorActivity collection");
}

function appointmentStats(dbName, colName) {
    const result = db.getSiblingDB(dbName).getCollection(colName).aggregate([
        {
            $unwind: "$appointments"             // Unwind the appointments array
        },
        {
            $group: {
                _id: "$doctor",                  // Group by doctor's name
                totalAppointments: { $sum: 1 }   // Count the total number of appointments
            }
        }
    ]).toArray();
    
    db.getSiblingDB(dbName).getCollection("Appointments").insertMany(result);
    print("Appointment statistics inserted into Appointments collection");
}
