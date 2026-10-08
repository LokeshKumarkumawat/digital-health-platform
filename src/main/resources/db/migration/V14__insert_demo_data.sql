-- ============================================================
-- V15: INSERT COMPLETE DEMO DATA
-- 50 Indian Doctors, 30 Patients, Appointments, Consultations,
-- Payments, Invoices — Realistic Indian Healthcare Data
-- ============================================================

-- ============================================================
-- 1. USERS — Doctor Users (50 doctors)
-- ============================================================

INSERT INTO users (id, name, email, password, auth_provider, has_profile_complete, created_at, updated_at, version)
VALUES
-- Doctors (user_id 100-149)
(100, 'Dr. Rajesh Kumar Sharma',    'dr.rajesh.sharma@digitalhealth.in',    '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL', true, NOW() - INTERVAL '180 days', NOW(), 0),
(101, 'Dr. Priya Patel',            'dr.priya.patel@digitalhealth.in',      '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL', true, NOW() - INTERVAL '175 days', NOW(), 0),
(102, 'Dr. Amit Singh Rathore',     'dr.amit.rathore@digitalhealth.in',     '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL', true, NOW() - INTERVAL '170 days', NOW(), 0),
(103, 'Dr. Sunita Devi Gupta',      'dr.sunita.gupta@digitalhealth.in',     '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL', true, NOW() - INTERVAL '165 days', NOW(), 0),
(104, 'Dr. Vikram Mehta',           'dr.vikram.mehta@digitalhealth.in',     '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL', true, NOW() - INTERVAL '160 days', NOW(), 0),
(105, 'Dr. Ananya Krishnamurthy',   'dr.ananya.krishna@digitalhealth.in',   '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL', true, NOW() - INTERVAL '155 days', NOW(), 0),
(106, 'Dr. Sanjay Verma',           'dr.sanjay.verma@digitalhealth.in',     '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL', true, NOW() - INTERVAL '150 days', NOW(), 0),
(107, 'Dr. Deepa Nair',             'dr.deepa.nair@digitalhealth.in',       '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL', true, NOW() - INTERVAL '145 days', NOW(), 0),
(108, 'Dr. Arjun Reddy',            'dr.arjun.reddy@digitalhealth.in',      '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL', true, NOW() - INTERVAL '140 days', NOW(), 0),
(109, 'Dr. Kavitha Iyer',           'dr.kavitha.iyer@digitalhealth.in',     '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL', true, NOW() - INTERVAL '135 days', NOW(), 0),
(110, 'Dr. Manoj Tiwari',           'dr.manoj.tiwari@digitalhealth.in',     '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL', true, NOW() - INTERVAL '130 days', NOW(), 0),
(111, 'Dr. Neha Agarwal',           'dr.neha.agarwal@digitalhealth.in',     '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL', true, NOW() - INTERVAL '125 days', NOW(), 0),
(112, 'Dr. Karthik Subramanian',    'dr.karthik.subra@digitalhealth.in',    '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL', true, NOW() - INTERVAL '120 days', NOW(), 0),
(113, 'Dr. Pooja Joshi',            'dr.pooja.joshi@digitalhealth.in',      '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL', true, NOW() - INTERVAL '115 days', NOW(), 0),
(114, 'Dr. Rahul Deshmukh',         'dr.rahul.deshmukh@digitalhealth.in',   '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL', true, NOW() - INTERVAL '110 days', NOW(), 0),
(115, 'Dr. Meena Kumari Yadav',     'dr.meena.yadav@digitalhealth.in',      '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL', true, NOW() - INTERVAL '105 days', NOW(), 0),
(116, 'Dr. Suresh Babu Pillai',     'dr.suresh.pillai@digitalhealth.in',    '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL', true, NOW() - INTERVAL '100 days', NOW(), 0),
(117, 'Dr. Rashmi Saxena',          'dr.rashmi.saxena@digitalhealth.in',    '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL', true, NOW() - INTERVAL '95 days',  NOW(), 0),
(118, 'Dr. Venkatesh Prasad',       'dr.venkatesh.prasad@digitalhealth.in', '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL', true, NOW() - INTERVAL '90 days',  NOW(), 0),
(119, 'Dr. Anjali Chopra',          'dr.anjali.chopra@digitalhealth.in',    '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL', true, NOW() - INTERVAL '85 days',  NOW(), 0),
(120, 'Dr. Prakash Chandra Mishra', 'dr.prakash.mishra@digitalhealth.in',   '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL', true, NOW() - INTERVAL '80 days',  NOW(), 0),
(121, 'Dr. Lakshmi Menon',          'dr.lakshmi.menon@digitalhealth.in',    '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL', true, NOW() - INTERVAL '75 days',  NOW(), 0),
(122, 'Dr. Ashok Kumar Jain',       'dr.ashok.jain@digitalhealth.in',       '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL', true, NOW() - INTERVAL '70 days',  NOW(), 0),
(123, 'Dr. Smita Patil',            'dr.smita.patil@digitalhealth.in',      '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL', true, NOW() - INTERVAL '65 days',  NOW(), 0),
(124, 'Dr. Gaurav Kapoor',          'dr.gaurav.kapoor@digitalhealth.in',    '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL', true, NOW() - INTERVAL '60 days',  NOW(), 0),
(125, 'Dr. Revathi Shankar',        'dr.revathi.shankar@digitalhealth.in',  '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL', true, NOW() - INTERVAL '55 days',  NOW(), 0),
(126, 'Dr. Nitin Srivastava',       'dr.nitin.srivastava@digitalhealth.in', '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL', true, NOW() - INTERVAL '50 days',  NOW(), 0),
(127, 'Dr. Shweta Bhatt',           'dr.shweta.bhatt@digitalhealth.in',     '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL', true, NOW() - INTERVAL '48 days',  NOW(), 0),
(128, 'Dr. Ramesh Chandra Pandey',  'dr.ramesh.pandey@digitalhealth.in',    '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL', true, NOW() - INTERVAL '45 days',  NOW(), 0),
(129, 'Dr. Divya Raghavan',         'dr.divya.raghavan@digitalhealth.in',   '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL', true, NOW() - INTERVAL '42 days',  NOW(), 0),
(130, 'Dr. Harish Rawat',           'dr.harish.rawat@digitalhealth.in',     '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL', true, NOW() - INTERVAL '40 days',  NOW(), 0),
(131, 'Dr. Pallavi Kulkarni',       'dr.pallavi.kulkarni@digitalhealth.in', '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL', true, NOW() - INTERVAL '38 days',  NOW(), 0),
(132, 'Dr. Arun Bhatia',            'dr.arun.bhatia@digitalhealth.in',      '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL', true, NOW() - INTERVAL '36 days',  NOW(), 0),
(133, 'Dr. Gayatri Deshpande',      'dr.gayatri.deshpande@digitalhealth.in','$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL', true, NOW() - INTERVAL '34 days',  NOW(), 0),
(134, 'Dr. Mohit Malhotra',         'dr.mohit.malhotra@digitalhealth.in',   '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL', true, NOW() - INTERVAL '32 days',  NOW(), 0),
(135, 'Dr. Sneha Reddy',            'dr.sneha.reddy@digitalhealth.in',      '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL', true, NOW() - INTERVAL '30 days',  NOW(), 0),
(136, 'Dr. Vijay Shankar Dubey',    'dr.vijay.dubey@digitalhealth.in',      '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL', true, NOW() - INTERVAL '28 days',  NOW(), 0),
(137, 'Dr. Ritu Khanna',            'dr.ritu.khanna@digitalhealth.in',      '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL', true, NOW() - INTERVAL '26 days',  NOW(), 0),
(138, 'Dr. Santosh Kumar Rao',      'dr.santosh.rao@digitalhealth.in',      '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL', true, NOW() - INTERVAL '24 days',  NOW(), 0),
(139, 'Dr. Aparna Mukherjee',       'dr.aparna.mukherjee@digitalhealth.in', '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL', true, NOW() - INTERVAL '22 days',  NOW(), 0),
(140, 'Dr. Naveen Chawla',          'dr.naveen.chawla@digitalhealth.in',    '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL', true, NOW() - INTERVAL '20 days',  NOW(), 0),
(141, 'Dr. Bhavna Choudhary',       'dr.bhavna.choudhary@digitalhealth.in', '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL', true, NOW() - INTERVAL '18 days',  NOW(), 0),
(142, 'Dr. Rakesh Mohan Gupta',     'dr.rakesh.gupta@digitalhealth.in',     '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL', true, NOW() - INTERVAL '16 days',  NOW(), 0),
(143, 'Dr. Tanuja Banerjee',        'dr.tanuja.banerjee@digitalhealth.in',  '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL', true, NOW() - INTERVAL '14 days',  NOW(), 0),
(144, 'Dr. Dinesh Thakur',          'dr.dinesh.thakur@digitalhealth.in',    '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL', true, NOW() - INTERVAL '12 days',  NOW(), 0),
(145, 'Dr. Jyoti Sinha',            'dr.jyoti.sinha@digitalhealth.in',      '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL', true, NOW() - INTERVAL '10 days',  NOW(), 0),
(146, 'Dr. Pankaj Sharma',          'dr.pankaj.sharma@digitalhealth.in',    '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL', true, NOW() - INTERVAL '8 days',   NOW(), 0),
(147, 'Dr. Madhuri Dixit',          'dr.madhuri.dixit@digitalhealth.in',    '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL', true, NOW() - INTERVAL '6 days',   NOW(), 0),
(148, 'Dr. Sunil Kumar Yadav',      'dr.sunil.yadav@digitalhealth.in',      '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL', true, NOW() - INTERVAL '4 days',   NOW(), 0),
(149, 'Dr. Rekha Bhandari',         'dr.rekha.bhandari@digitalhealth.in',   '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL', true, NOW() - INTERVAL '2 days',   NOW(), 0)
    ON CONFLICT (email) DO NOTHING;

-- ============================================================
-- 2. USERS — Patient Users (30 patients)
-- ============================================================

INSERT INTO users (id, name, email, password, auth_provider, has_profile_complete, created_at, updated_at, version)
VALUES
    (200, 'Aarav Sharma',        'aarav.sharma@gmail.com',        '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL',  true, NOW() - INTERVAL '90 days', NOW(), 0),
    (201, 'Diya Patel',          'diya.patel@gmail.com',          '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'GOOGLE', true, NOW() - INTERVAL '85 days', NOW(), 0),
    (202, 'Vihaan Singh',        'vihaan.singh@gmail.com',        '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL',  true, NOW() - INTERVAL '80 days', NOW(), 0),
    (203, 'Ananya Reddy',        'ananya.reddy@gmail.com',        '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'GOOGLE', true, NOW() - INTERVAL '75 days', NOW(), 0),
    (204, 'Arjun Kumar',         'arjun.kumar@gmail.com',         '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL',  true, NOW() - INTERVAL '70 days', NOW(), 0),
    (205, 'Saanvi Gupta',        'saanvi.gupta@gmail.com',        '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL',  true, NOW() - INTERVAL '65 days', NOW(), 0),
    (206, 'Reyansh Joshi',       'reyansh.joshi@gmail.com',       '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'GOOGLE', true, NOW() - INTERVAL '60 days', NOW(), 0),
    (207, 'Ishita Nair',         'ishita.nair@gmail.com',         '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL',  true, NOW() - INTERVAL '55 days', NOW(), 0),
    (208, 'Kabir Deshmukh',      'kabir.deshmukh@gmail.com',      '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL',  true, NOW() - INTERVAL '50 days', NOW(), 0),
    (209, 'Myra Iyer',           'myra.iyer@gmail.com',           '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'GOOGLE', true, NOW() - INTERVAL '45 days', NOW(), 0),
    (210, 'Aditya Verma',        'aditya.verma@gmail.com',        '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL',  true, NOW() - INTERVAL '42 days', NOW(), 0),
    (211, 'Kiara Chopra',        'kiara.chopra@gmail.com',        '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL',  true, NOW() - INTERVAL '40 days', NOW(), 0),
    (212, 'Vivaan Tiwari',       'vivaan.tiwari@gmail.com',       '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'GOOGLE', true, NOW() - INTERVAL '38 days', NOW(), 0),
    (213, 'Aisha Khan',          'aisha.khan@gmail.com',          '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL',  true, NOW() - INTERVAL '36 days', NOW(), 0),
    (214, 'Dhruv Mehta',         'dhruv.mehta@gmail.com',         '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL',  true, NOW() - INTERVAL '34 days', NOW(), 0),
    (215, 'Pari Saxena',         'pari.saxena@gmail.com',         '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'GOOGLE', true, NOW() - INTERVAL '32 days', NOW(), 0),
    (216, 'Ritvik Pandey',       'ritvik.pandey@gmail.com',       '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL',  true, NOW() - INTERVAL '30 days', NOW(), 0),
    (217, 'Navya Kulkarni',      'navya.kulkarni@gmail.com',      '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL',  true, NOW() - INTERVAL '28 days', NOW(), 0),
    (218, 'Sai Kiran Reddy',     'saikiran.reddy@gmail.com',      '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'GOOGLE', true, NOW() - INTERVAL '26 days', NOW(), 0),
    (219, 'Riya Banerjee',       'riya.banerjee@gmail.com',       '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL',  true, NOW() - INTERVAL '24 days', NOW(), 0),
    (220, 'Om Prakash Mishra',   'om.mishra@gmail.com',           '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL',  true, NOW() - INTERVAL '22 days', NOW(), 0),
    (221, 'Tara Choudhary',      'tara.choudhary@gmail.com',      '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'GOOGLE', true, NOW() - INTERVAL '20 days', NOW(), 0),
    (222, 'Harsh Agarwal',       'harsh.agarwal@gmail.com',       '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL',  true, NOW() - INTERVAL '18 days', NOW(), 0),
    (223, 'Meera Pillai',        'meera.pillai@gmail.com',        '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL',  true, NOW() - INTERVAL '16 days', NOW(), 0),
    (224, 'Arnav Bhatia',        'arnav.bhatia@gmail.com',        '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'GOOGLE', true, NOW() - INTERVAL '14 days', NOW(), 0),
    (225, 'Sanya Malhotra',      'sanya.malhotra@gmail.com',      '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL',  true, NOW() - INTERVAL '12 days', NOW(), 0),
    (226, 'Rohan Thakur',        'rohan.thakur@gmail.com',        '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL',  true, NOW() - INTERVAL '10 days', NOW(), 0),
    (227, 'Ira Kapoor',          'ira.kapoor@gmail.com',          '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'GOOGLE', true, NOW() - INTERVAL '8 days',  NOW(), 0),
    (228, 'Yash Rawat',          'yash.rawat@gmail.com',          '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'LOCAL',  true, NOW() - INTERVAL '6 days',  NOW(), 0),
    (229, 'Zara Sheikh',         'zara.sheikh@gmail.com',         '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu', 'GOOGLE', true, NOW() - INTERVAL '4 days',  NOW(), 0)
    ON CONFLICT (email) DO NOTHING;

-- ============================================================
-- 3. ASSIGN ROLES
-- ============================================================

-- Doctor Role (role_id = 2 for ROLE_DOCTOR based on V11)
INSERT INTO user_roles (user_id, role_id)
SELECT id, 2 FROM users WHERE id BETWEEN 100 AND 149
    ON CONFLICT DO NOTHING;

-- Patient Role (role_id = 1 for ROLE_PATIENT)
INSERT INTO user_roles (user_id, role_id)
SELECT id, 1 FROM users WHERE id BETWEEN 200 AND 229
    ON CONFLICT DO NOTHING;

-- ============================================================
-- 4. DOCTORS TABLE (50 Doctors with Indian Specializations)
-- ============================================================

INSERT INTO doctors (id, first_name, last_name, specialization, license_number, user_id, created_at, updated_at, version)
VALUES
    (1,  'Rajesh',   'Sharma',        'GENERAL_PRACTICE',          'MCI-2015-DL-00101', 100, NOW() - INTERVAL '180 days', NOW(), 0),
    (2,  'Priya',    'Patel',         'OBSTETRICS_GYNAECOLOGY',    'MCI-2014-GJ-00202', 101, NOW() - INTERVAL '175 days', NOW(), 0),
    (3,  'Amit',     'Rathore',       'CARDIOLOGY',                'MCI-2012-RJ-00303', 102, NOW() - INTERVAL '170 days', NOW(), 0),
    (4,  'Sunita',   'Gupta',         'PEDIATRICS',                'MCI-2016-UP-00404', 103, NOW() - INTERVAL '165 days', NOW(), 0),
    (5,  'Vikram',   'Mehta',         'NEUROLOGY',                 'MCI-2013-MH-00505', 104, NOW() - INTERVAL '160 days', NOW(), 0),
    (6,  'Ananya',   'Krishnamurthy', 'DERMATOLOGY',               'MCI-2017-KA-00606', 105, NOW() - INTERVAL '155 days', NOW(), 0),
    (7,  'Sanjay',   'Verma',         'ORTHOPAEDICS',              'MCI-2011-MP-00707', 106, NOW() - INTERVAL '150 days', NOW(), 0),
    (8,  'Deepa',    'Nair',          'PSYCHIATRY',                'MCI-2015-KL-00808', 107, NOW() - INTERVAL '145 days', NOW(), 0),
    (9,  'Arjun',    'Reddy',         'UROLOGY',                   'MCI-2014-AP-00909', 108, NOW() - INTERVAL '140 days', NOW(), 0),
    (10, 'Kavitha',  'Iyer',          'ENDOCRINOLOGY',             'MCI-2016-TN-01010', 109, NOW() - INTERVAL '135 days', NOW(), 0),
    (11, 'Manoj',    'Tiwari',        'GASTROENTEROLOGY',          'MCI-2013-BH-01111', 110, NOW() - INTERVAL '130 days', NOW(), 0),
    (12, 'Neha',     'Agarwal',       'PULMONOLOGY',               'MCI-2017-UP-01212', 111, NOW() - INTERVAL '125 days', NOW(), 0),
    (13, 'Karthik',  'Subramanian',   'NEPHROLOGY',                'MCI-2012-TN-01313', 112, NOW() - INTERVAL '120 days', NOW(), 0),
    (14, 'Pooja',    'Joshi',         'OPHTHALMOLOGY',             'MCI-2015-UK-01414', 113, NOW() - INTERVAL '115 days', NOW(), 0),
    (15, 'Rahul',    'Deshmukh',      'ENT',                       'MCI-2014-MH-01515', 114, NOW() - INTERVAL '110 days', NOW(), 0),
    (16, 'Meena',    'Yadav',         'DENTISTRY',                 'DCI-2016-HR-01616', 115, NOW() - INTERVAL '105 days', NOW(), 0),
    (17, 'Suresh',   'Pillai',        'GENERAL_SURGERY',           'MCI-2011-KL-01717', 116, NOW() - INTERVAL '100 days', NOW(), 0),
    (18, 'Rashmi',   'Saxena',        'RHEUMATOLOGY',              'MCI-2015-DL-01818', 117, NOW() - INTERVAL '95 days',  NOW(), 0),
    (19, 'Venkatesh','Prasad',        'DIABETOLOGY',               'MCI-2013-KA-01919', 118, NOW() - INTERVAL '90 days',  NOW(), 0),
    (20, 'Anjali',   'Chopra',        'INTERNAL_MEDICINE',         'MCI-2016-PB-02020', 119, NOW() - INTERVAL '85 days',  NOW(), 0),
    (21, 'Prakash',  'Mishra',        'EMERGENCY_MEDICINE',        'MCI-2012-UP-02121', 120, NOW() - INTERVAL '80 days',  NOW(), 0),
    (22, 'Lakshmi',  'Menon',         'MEDICAL_ONCOLOGY',          'MCI-2014-KL-02222', 121, NOW() - INTERVAL '75 days',  NOW(), 0),
    (23, 'Ashok',    'Jain',          'INFECTIOUS_DISEASE',        'MCI-2015-RJ-02323', 122, NOW() - INTERVAL '70 days',  NOW(), 0),
    (24, 'Smita',    'Patil',         'NEUROSURGERY',              'MCI-2011-MH-02424', 123, NOW() - INTERVAL '65 days',  NOW(), 0),
    (25, 'Gaurav',   'Kapoor',        'PSYCHOLOGY',                'RCI-2016-DL-02525', 124, NOW() - INTERVAL '60 days',  NOW(), 0),
    (26, 'Revathi',  'Shankar',       'OBSTETRICS_GYNAECOLOGY',    'MCI-2017-TN-02626', 125, NOW() - INTERVAL '55 days',  NOW(), 0),
    (27, 'Nitin',    'Srivastava',    'CARDIOLOGY',                'MCI-2013-UP-02727', 126, NOW() - INTERVAL '50 days',  NOW(), 0),
    (28, 'Shweta',   'Bhatt',         'DERMATOLOGY',               'MCI-2015-UK-02828', 127, NOW() - INTERVAL '48 days',  NOW(), 0),
    (29, 'Ramesh',   'Pandey',        'GENERAL_PRACTICE',          'MCI-2012-UP-02929', 128, NOW() - INTERVAL '45 days',  NOW(), 0),
    (30, 'Divya',    'Raghavan',      'PEDIATRICS',                'MCI-2016-KA-03030', 129, NOW() - INTERVAL '42 days',  NOW(), 0),
    (31, 'Harish',   'Rawat',         'ORTHOPAEDICS',              'MCI-2014-UK-03131', 130, NOW() - INTERVAL '40 days',  NOW(), 0),
    (32, 'Pallavi',  'Kulkarni',      'ENDOCRINOLOGY',             'MCI-2017-MH-03232', 131, NOW() - INTERVAL '38 days',  NOW(), 0),
    (33, 'Arun',     'Bhatia',        'GASTROENTEROLOGY',          'MCI-2013-PB-03333', 132, NOW() - INTERVAL '36 days',  NOW(), 0),
    (34, 'Gayatri',  'Deshpande',     'PULMONOLOGY',               'MCI-2015-MH-03434', 133, NOW() - INTERVAL '34 days',  NOW(), 0),
    (35, 'Mohit',    'Malhotra',      'UROLOGY',                   'MCI-2014-DL-03535', 134, NOW() - INTERVAL '32 days',  NOW(), 0),
    (36, 'Sneha',    'Reddy',         'NEPHROLOGY',                'MCI-2016-TS-03636', 135, NOW() - INTERVAL '30 days',  NOW(), 0),
    (37, 'Vijay',    'Dubey',         'NEUROLOGY',                 'MCI-2012-MP-03737', 136, NOW() - INTERVAL '28 days',  NOW(), 0),
    (38, 'Ritu',     'Khanna',        'ALLERGIST_IMMUNOLOGY',      'MCI-2015-DL-03838', 137, NOW() - INTERVAL '26 days',  NOW(), 0),
    (39, 'Santosh',  'Rao',           'SURGICAL_GASTROENTEROLOGY', 'MCI-2013-KA-03939', 138, NOW() - INTERVAL '24 days',  NOW(), 0),
    (40, 'Aparna',   'Mukherjee',     'PSYCHIATRY',                'MCI-2016-WB-04040', 139, NOW() - INTERVAL '22 days',  NOW(), 0),
    (41, 'Naveen',   'Chawla',        'ANAESTHESIA',               'MCI-2014-PB-04141', 140, NOW() - INTERVAL '20 days',  NOW(), 0),
    (42, 'Bhavna',   'Choudhary',     'PHYSIOTHERAPY',             'IAP-2017-RJ-04242', 141, NOW() - INTERVAL '18 days',  NOW(), 0),
    (43, 'Rakesh',   'Gupta',         'RADIOLOGY',                 'MCI-2013-UP-04343', 142, NOW() - INTERVAL '16 days',  NOW(), 0),
    (44, 'Tanuja',   'Banerjee',      'PATHOLOGY',                 'MCI-2015-WB-04444', 143, NOW() - INTERVAL '14 days',  NOW(), 0),
    (45, 'Dinesh',   'Thakur',        'ANDROLOGY',                 'MCI-2014-HP-04545', 144, NOW() - INTERVAL '12 days',  NOW(), 0),
    (46, 'Jyoti',    'Sinha',         'AYURVEDA',                  'CCIM-2016-BH-04646',145, NOW() - INTERVAL '10 days',  NOW(), 0),
    (47, 'Pankaj',   'Sharma',        'HOMEOPATHY',                'CCH-2015-DL-04747', 146, NOW() - INTERVAL '8 days',   NOW(), 0),
    (48, 'Madhuri',  'Dixit',         'OBSTETRICS_GYNAECOLOGY',    'MCI-2017-MH-04848', 147, NOW() - INTERVAL '6 days',   NOW(), 0),
    (49, 'Sunil',    'Yadav',         'GENERAL_PRACTICE',          'MCI-2014-HR-04949', 148, NOW() - INTERVAL '4 days',   NOW(), 0),
    (50, 'Rekha',    'Bhandari',      'DENTISTRY',                 'DCI-2016-UK-05050', 149, NOW() - INTERVAL '2 days',   NOW(), 0)
    ON CONFLICT (license_number) DO NOTHING;

-- ============================================================
-- 5. PATIENTS TABLE (30 Patients with Indian Data)
-- ============================================================

INSERT INTO patients (id, first_name, last_name, date_of_birth, phone, known_allergies, blood_group, genotype, user_id, created_at, updated_at, version)
VALUES
    (1,  'Aarav',    'Sharma',    '1990-03-15', '+919876543210', NULL,                            'B_POSITIVE',  'AA', 200, NOW() - INTERVAL '90 days', NOW(), 0),
    (2,  'Diya',     'Patel',     '1985-07-22', '+919876543211', 'Penicillin, Sulfa drugs',       'O_POSITIVE',  'AS', 201, NOW() - INTERVAL '85 days', NOW(), 0),
    (3,  'Vihaan',   'Singh',     '1978-11-08', '+919876543212', NULL,                            'A_POSITIVE',  'AA', 202, NOW() - INTERVAL '80 days', NOW(), 0),
    (4,  'Ananya',   'Reddy',     '1995-01-30', '+919876543213', 'Peanuts',                       'AB_POSITIVE', 'AA', 203, NOW() - INTERVAL '75 days', NOW(), 0),
    (5,  'Arjun',    'Kumar',     '1982-05-17', '+919876543214', NULL,                            'B_NEGATIVE',  'AS', 204, NOW() - INTERVAL '70 days', NOW(), 0),
    (6,  'Saanvi',   'Gupta',     '1998-09-25', '+919876543215', 'Dust mites, Aspirin',           'O_NEGATIVE',  'AA', 205, NOW() - INTERVAL '65 days', NOW(), 0),
    (7,  'Reyansh',  'Joshi',     '2000-12-12', '+919876543216', NULL,                            'A_NEGATIVE',  'AA', 206, NOW() - INTERVAL '60 days', NOW(), 0),
    (8,  'Ishita',   'Nair',      '1988-06-03', '+919876543217', 'Shellfish, Ibuprofen',          'B_POSITIVE',  'AS', 207, NOW() - INTERVAL '55 days', NOW(), 0),
    (9,  'Kabir',    'Deshmukh',  '1975-08-19', '+919876543218', NULL,                            'O_POSITIVE',  'AA', 208, NOW() - INTERVAL '50 days', NOW(), 0),
    (10, 'Myra',     'Iyer',      '1992-02-14', '+919876543219', 'Latex',                         'AB_NEGATIVE', 'AA', 209, NOW() - INTERVAL '45 days', NOW(), 0),
    (11, 'Aditya',   'Verma',     '1987-04-28', '+919876543220', NULL,                            'A_POSITIVE',  'AA', 210, NOW() - INTERVAL '42 days', NOW(), 0),
    (12, 'Kiara',    'Chopra',    '1993-10-07', '+919876543221', 'Amoxicillin',                   'B_POSITIVE',  'AS', 211, NOW() - INTERVAL '40 days', NOW(), 0),
    (13, 'Vivaan',   'Tiwari',    '2005-03-21', '+919876543222', NULL,                            'O_POSITIVE',  'AA', 212, NOW() - INTERVAL '38 days', NOW(), 0),
    (14, 'Aisha',    'Khan',      '1996-07-11', '+919876543223', 'Gluten, Dairy',                 'A_NEGATIVE',  'AA', 213, NOW() - INTERVAL '36 days', NOW(), 0),
    (15, 'Dhruv',    'Mehta',     '1980-01-05', '+919876543224', NULL,                            'B_NEGATIVE',  'AS', 214, NOW() - INTERVAL '34 days', NOW(), 0),
    (16, 'Pari',     'Saxena',    '2001-11-18', '+919876543225', 'Egg, Soy',                      'AB_POSITIVE', 'AA', 215, NOW() - INTERVAL '32 days', NOW(), 0),
    (17, 'Ritvik',   'Pandey',    '1983-09-09', '+919876543226', NULL,                            'O_POSITIVE',  'AA', 216, NOW() - INTERVAL '30 days', NOW(), 0),
    (18, 'Navya',    'Kulkarni',  '1997-05-26', '+919876543227', 'Codeine, Morphine',             'B_POSITIVE',  'AS', 217, NOW() - INTERVAL '28 days', NOW(), 0),
    (19, 'Sai Kiran','Reddy',     '1991-08-14', '+919876543228', NULL,                            'A_POSITIVE',  'AA', 218, NOW() - INTERVAL '26 days', NOW(), 0),
    (20, 'Riya',     'Banerjee',  '1986-12-01', '+919876543229', 'NSAID drugs',                   'O_NEGATIVE',  'AA', 219, NOW() - INTERVAL '24 days', NOW(), 0),
    (21, 'Om',       'Mishra',    '1970-06-20', '+919876543230', 'Metformin',                     'B_NEGATIVE',  'AS', 220, NOW() - INTERVAL '22 days', NOW(), 0),
    (22, 'Tara',     'Choudhary', '1994-04-13', '+919876543231', NULL,                            'AB_POSITIVE', 'AA', 221, NOW() - INTERVAL '20 days', NOW(), 0),
    (23, 'Harsh',    'Agarwal',   '1989-10-30', '+919876543232', 'Contrast dye',                  'A_POSITIVE',  'AA', 222, NOW() - INTERVAL '18 days', NOW(), 0),
    (24, 'Meera',    'Pillai',    '1976-02-07', '+919876543233', NULL,                            'O_POSITIVE',  'AS', 223, NOW() - INTERVAL '16 days', NOW(), 0),
    (25, 'Arnav',    'Bhatia',    '2003-08-25', '+919876543234', 'Pollen, Cat dander',            'B_POSITIVE',  'AA', 224, NOW() - INTERVAL '14 days', NOW(), 0),
    (26, 'Sanya',    'Malhotra',  '1999-06-16', '+919876543235', NULL,                            'A_NEGATIVE',  'AA', 225, NOW() - INTERVAL '12 days', NOW(), 0),
    (27, 'Rohan',    'Thakur',    '1984-01-29', '+919876543236', 'Sulfonamides',                  'AB_NEGATIVE', 'AS', 226, NOW() - INTERVAL '10 days', NOW(), 0),
    (28, 'Ira',      'Kapoor',    '1992-07-04', '+919876543237', NULL,                            'B_POSITIVE',  'AA', 227, NOW() - INTERVAL '8 days',  NOW(), 0),
    (29, 'Yash',     'Rawat',     '1981-11-11', '+919876543238', 'Bee venom, Wasp stings',        'O_POSITIVE',  'AA', 228, NOW() - INTERVAL '6 days',  NOW(), 0),
    (30, 'Zara',     'Sheikh',    '1996-03-08', '+919876543239', NULL,                            'A_POSITIVE',  'AA', 229, NOW() - INTERVAL '4 days',  NOW(), 0)
    ON CONFLICT (user_id) DO NOTHING;

-- ============================================================
-- 6. APPOINTMENTS (40 Appointments)
-- ============================================================

INSERT INTO appointments (id, start_time, end_time, meeting_link, purpose_of_consultation, initial_symptoms, status, doctor_id, patient_id, created_at, updated_at, version)
VALUES
-- Completed Appointments (Past)
(1,  NOW() - INTERVAL '60 days' + TIME '09:00', NOW() - INTERVAL '60 days' + TIME '09:30', 'https://meet.google.com/abc-defg-hij', 'Routine health checkup and blood pressure monitoring', 'Mild headache, fatigue for 2 weeks', 'COMPLETED', 1,  1,  NOW() - INTERVAL '65 days', NOW(), 0),
(2,  NOW() - INTERVAL '55 days' + TIME '10:00', NOW() - INTERVAL '55 days' + TIME '10:30', 'https://meet.google.com/bcd-efgh-ijk', 'Irregular periods and hormonal issues', 'Irregular menstrual cycle, mood swings', 'COMPLETED', 2,  2,  NOW() - INTERVAL '60 days', NOW(), 0),
(3,  NOW() - INTERVAL '50 days' + TIME '11:00', NOW() - INTERVAL '50 days' + TIME '11:45', 'https://meet.google.com/cde-fghi-jkl', 'Chest pain evaluation and ECG review', 'Chest tightness, breathlessness on exertion', 'COMPLETED', 3,  3,  NOW() - INTERVAL '55 days', NOW(), 0),
(4,  NOW() - INTERVAL '45 days' + TIME '14:00', NOW() - INTERVAL '45 days' + TIME '14:30', 'https://meet.google.com/def-ghij-klm', 'Child vaccination schedule and development check', 'Fever after vaccination, mild rash', 'COMPLETED', 4,  4,  NOW() - INTERVAL '50 days', NOW(), 0),
(5,  NOW() - INTERVAL '40 days' + TIME '15:00', NOW() - INTERVAL '40 days' + TIME '15:45', 'https://meet.google.com/efg-hijk-lmn', 'Recurring migraine headaches', 'Severe headaches with aura, nausea, sensitivity to light', 'COMPLETED', 5,  5,  NOW() - INTERVAL '45 days', NOW(), 0),
(6,  NOW() - INTERVAL '35 days' + TIME '09:30', NOW() - INTERVAL '35 days' + TIME '10:00', 'https://meet.google.com/fgh-ijkl-mno', 'Acne treatment and skincare consultation', 'Persistent acne on face and back, oily skin', 'COMPLETED', 6,  6,  NOW() - INTERVAL '40 days', NOW(), 0),
(7,  NOW() - INTERVAL '30 days' + TIME '10:30', NOW() - INTERVAL '30 days' + TIME '11:00', 'https://meet.google.com/ghi-jklm-nop', 'Knee pain and joint stiffness', 'Pain in right knee for 3 months, difficulty climbing stairs', 'COMPLETED', 7,  7,  NOW() - INTERVAL '35 days', NOW(), 0),
(8,  NOW() - INTERVAL '25 days' + TIME '16:00', NOW() - INTERVAL '25 days' + TIME '16:45', 'https://meet.google.com/hij-klmn-opq', 'Anxiety and sleep disorder treatment', 'Anxiety attacks, insomnia for 1 month, palpitations', 'COMPLETED', 8,  8,  NOW() - INTERVAL '30 days', NOW(), 0),
(9,  NOW() - INTERVAL '20 days' + TIME '11:30', NOW() - INTERVAL '20 days' + TIME '12:00', 'https://meet.google.com/ijk-lmno-pqr', 'Urinary tract infection symptoms', 'Burning during urination, increased frequency', 'COMPLETED', 9,  9,  NOW() - INTERVAL '25 days', NOW(), 0),
(10, NOW() - INTERVAL '15 days' + TIME '14:30', NOW() - INTERVAL '15 days' + TIME '15:00', 'https://meet.google.com/jkl-mnop-qrs', 'Thyroid function review and medication adjustment', 'Weight gain, hair loss, tiredness', 'COMPLETED', 10, 10, NOW() - INTERVAL '20 days', NOW(), 0),
(11, NOW() - INTERVAL '12 days' + TIME '09:00', NOW() - INTERVAL '12 days' + TIME '09:30', NULL, 'Acid reflux and stomach pain', 'Heartburn after meals, bloating, gas', 'COMPLETED', 11, 11, NOW() - INTERVAL '17 days', NOW(), 0),
(12, NOW() - INTERVAL '10 days' + TIME '10:00', NOW() - INTERVAL '10 days' + TIME '10:30', NULL, 'Chronic cough and breathing difficulty', 'Persistent cough for 6 weeks, wheezing at night', 'COMPLETED', 12, 12, NOW() - INTERVAL '15 days', NOW(), 0),
(13, NOW() - INTERVAL '8 days' + TIME '11:00', NOW() - INTERVAL '8 days' + TIME '11:30', NULL, 'Kidney function test review', 'Swelling in legs, decreased urine output', 'COMPLETED', 13, 13, NOW() - INTERVAL '13 days', NOW(), 0),
(14, NOW() - INTERVAL '6 days' + TIME '15:00', NOW() - INTERVAL '6 days' + TIME '15:30', NULL, 'Eye checkup and vision test', 'Blurry vision, eye strain from computer work', 'COMPLETED', 14, 14, NOW() - INTERVAL '11 days', NOW(), 0),
(15, NOW() - INTERVAL '4 days' + TIME '09:30', NOW() - INTERVAL '4 days' + TIME '10:00', NULL, 'Ear infection and hearing issues', 'Ear pain, reduced hearing in left ear', 'COMPLETED', 15, 15, NOW() - INTERVAL '9 days', NOW(), 0),

-- Scheduled Appointments (Upcoming)
(16, NOW() + INTERVAL '1 day'  + TIME '09:00', NOW() + INTERVAL '1 day'  + TIME '09:30', 'https://meet.google.com/klm-nopq-rst', 'Dental cleaning and cavity check', 'Tooth sensitivity, mild gum bleeding', 'SCHEDULED', 16, 16, NOW() - INTERVAL '3 days', NOW(), 0),
(17, NOW() + INTERVAL '2 days' + TIME '10:00', NOW() + INTERVAL '2 days' + TIME '10:45', 'https://meet.google.com/lmn-opqr-stu', 'Hernia consultation', 'Bulge in lower abdomen, discomfort while lifting', 'SCHEDULED', 17, 17, NOW() - INTERVAL '3 days', NOW(), 0),
(18, NOW() + INTERVAL '3 days' + TIME '11:00', NOW() + INTERVAL '3 days' + TIME '11:30', 'https://meet.google.com/mno-pqrs-tuv', 'Joint pain and arthritis evaluation', 'Morning stiffness in fingers, swelling', 'SCHEDULED', 18, 18, NOW() - INTERVAL '2 days', NOW(), 0),
(19, NOW() + INTERVAL '3 days' + TIME '14:00', NOW() + INTERVAL '3 days' + TIME '14:30', 'https://meet.google.com/nop-qrst-uvw', 'Blood sugar management review', 'High fasting blood sugar, increased thirst', 'SCHEDULED', 19, 19, NOW() - INTERVAL '2 days', NOW(), 0),
(20, NOW() + INTERVAL '4 days' + TIME '15:00', NOW() + INTERVAL '4 days' + TIME '15:30', 'https://meet.google.com/opq-rstu-vwx', 'Fever and body aches follow-up', 'Recurring fever, joint pain, weakness', 'SCHEDULED', 20, 20, NOW() - INTERVAL '2 days', NOW(), 0),
(21, NOW() + INTERVAL '5 days' + TIME '09:00', NOW() + INTERVAL '5 days' + TIME '09:30', 'https://meet.google.com/pqr-stuv-wxy', 'Emergency consultation follow-up', 'Post-accident follow-up, rib pain', 'SCHEDULED', 21, 21, NOW() - INTERVAL '1 day', NOW(), 0),
(22, NOW() + INTERVAL '5 days' + TIME '10:30', NOW() + INTERVAL '5 days' + TIME '11:00', 'https://meet.google.com/qrs-tuvw-xyz', 'Cancer screening discussion', 'Family history of cancer, preventive screening', 'SCHEDULED', 22, 22, NOW() - INTERVAL '1 day', NOW(), 0),
(23, NOW() + INTERVAL '6 days' + TIME '14:00', NOW() + INTERVAL '6 days' + TIME '14:30', 'https://meet.google.com/rst-uvwx-yza', 'Dengue recovery follow-up', 'Post-dengue weakness, low platelet count', 'SCHEDULED', 23, 23, NOW() - INTERVAL '1 day', NOW(), 0),
(24, NOW() + INTERVAL '7 days' + TIME '11:00', NOW() + INTERVAL '7 days' + TIME '12:00', 'https://meet.google.com/stu-vwxy-zab', 'Brain MRI results discussion', 'Recurring headaches, dizziness', 'SCHEDULED', 24, 24, NOW(), NOW(), 0),
(25, NOW() + INTERVAL '7 days' + TIME '15:00', NOW() + INTERVAL '7 days' + TIME '15:45', 'https://meet.google.com/tuv-wxyz-abc', 'Stress and mental health counseling', 'Work-related stress, difficulty concentrating', 'SCHEDULED', 25, 25, NOW(), NOW(), 0),

-- More Scheduled Appointments
(26, NOW() + INTERVAL '8 days'  + TIME '09:00', NOW() + INTERVAL '8 days'  + TIME '09:30', NULL, 'Pregnancy checkup - second trimester', 'Routine pregnancy checkup, mild nausea', 'SCHEDULED', 26, 26, NOW(), NOW(), 0),
(27, NOW() + INTERVAL '9 days'  + TIME '10:00', NOW() + INTERVAL '9 days'  + TIME '10:30', NULL, 'Heart palpitations evaluation', 'Irregular heartbeat, anxiety', 'SCHEDULED', 27, 27, NOW(), NOW(), 0),
(28, NOW() + INTERVAL '10 days' + TIME '11:00', NOW() + INTERVAL '10 days' + TIME '11:30', NULL, 'Skin allergy treatment', 'Itchy rash on arms, redness', 'SCHEDULED', 28, 28, NOW(), NOW(), 0),
(29, NOW() + INTERVAL '11 days' + TIME '14:00', NOW() + INTERVAL '11 days' + TIME '14:30', NULL, 'Annual health checkup', 'No major symptoms, routine screening', 'SCHEDULED', 29, 29, NOW(), NOW(), 0),
(30, NOW() + INTERVAL '12 days' + TIME '15:00', NOW() + INTERVAL '12 days' + TIME '15:30', NULL, 'Child growth and nutrition assessment', 'Slow weight gain, picky eating', 'SCHEDULED', 30, 30, NOW(), NOW(), 0),

-- Cancelled Appointments
(31, NOW() - INTERVAL '25 days' + TIME '09:00', NOW() - INTERVAL '25 days' + TIME '09:30', NULL, 'Back pain consultation', 'Lower back pain radiating to legs', 'CANCELLED', 31, 1, NOW() - INTERVAL '30 days', NOW(), 0),
(32, NOW() - INTERVAL '20 days' + TIME '10:00', NOW() - INTERVAL '20 days' + TIME '10:30', NULL, 'Diabetes medication review', 'High blood sugar levels', 'CANCELLED', 32, 5, NOW() - INTERVAL '25 days', NOW(), 0),
(33, NOW() - INTERVAL '15 days' + TIME '11:00', NOW() - INTERVAL '15 days' + TIME '11:30', NULL, 'Stomach ulcer treatment', 'Burning stomach pain, nausea', 'CANCELLED', 33, 8, NOW() - INTERVAL '20 days', NOW(), 0),

-- No Show Appointments
(34, NOW() - INTERVAL '18 days' + TIME '14:00', NOW() - INTERVAL '18 days' + TIME '14:30', NULL, 'Asthma follow-up', 'Breathing difficulty at night, wheezing', 'NO_SHOW', 34, 12, NOW() - INTERVAL '23 days', NOW(), 0),
(35, NOW() - INTERVAL '14 days' + TIME '15:00', NOW() - INTERVAL '14 days' + TIME '15:30', NULL, 'Kidney stone treatment plan', 'Severe lower back pain, blood in urine', 'NO_SHOW', 35, 15, NOW() - INTERVAL '19 days', NOW(), 0),

-- More Completed (for variety)
(36, NOW() - INTERVAL '50 days' + TIME '09:00', NOW() - INTERVAL '50 days' + TIME '09:30', NULL, 'Diabetes management and diet plan', 'High HbA1c levels, increased thirst', 'COMPLETED', 19, 3,  NOW() - INTERVAL '55 days', NOW(), 0),
(37, NOW() - INTERVAL '45 days' + TIME '10:00', NOW() - INTERVAL '45 days' + TIME '10:30', NULL, 'Allergy testing and treatment', 'Seasonal sneezing, nasal congestion, watery eyes', 'COMPLETED', 38, 6,  NOW() - INTERVAL '50 days', NOW(), 0),
(38, NOW() - INTERVAL '40 days' + TIME '11:00', NOW() - INTERVAL '40 days' + TIME '11:30', NULL, 'Physiotherapy assessment for shoulder pain', 'Frozen shoulder, limited range of motion', 'COMPLETED', 42, 9,  NOW() - INTERVAL '45 days', NOW(), 0),
(39, NOW() - INTERVAL '35 days' + TIME '14:00', NOW() - INTERVAL '35 days' + TIME '14:30', NULL, 'Ayurvedic treatment for digestive issues', 'Chronic indigestion, bloating, gas', 'COMPLETED', 46, 14, NOW() - INTERVAL '40 days', NOW(), 0),
(40, NOW() - INTERVAL '30 days' + TIME '15:00', NOW() - INTERVAL '30 days' + TIME '15:30', NULL, 'Dental root canal treatment follow-up', 'Post-root canal sensitivity, mild swelling', 'COMPLETED', 50, 20, NOW() - INTERVAL '35 days', NOW(), 0)
    ON CONFLICT DO NOTHING;

-- ============================================================
-- 7. CONSULTATIONS (For Completed Appointments)
-- ============================================================

INSERT INTO consultations (id, consultation_date, subjective_notes, objective_findings, assessment, plan, appointment_id, created_at, updated_at, version)
VALUES
    (1,  NOW() - INTERVAL '60 days', 'Patient reports persistent headaches for 2 weeks, mostly in the evening. Associated fatigue and mild dizziness. No fever or vomiting. Family history of hypertension. Diet includes excessive salt and processed foods.', 'BP: 145/92 mmHg (elevated). HR: 78 bpm. BMI: 26.4 (overweight). Fundoscopy: normal. No papilledema. Neck: supple, no thyromegaly. Cardiovascular exam: S1S2 normal, no murmurs.', 'Stage 1 Hypertension (I10). Tension-type headache secondary to elevated blood pressure. Overweight (E66.9).', 'Start Amlodipine 5mg once daily. Low-salt diet (<5g/day). 30 minutes brisk walking daily. Reduce screen time before bed. Review in 4 weeks with home BP monitoring diary. Blood tests: FBS, lipid profile, serum creatinine, electrolytes.', 1, NOW() - INTERVAL '60 days', NOW(), 0),

    (2,  NOW() - INTERVAL '55 days', 'Patient Diya Patel, 39 years, complains of irregular menstrual cycles for 6 months. Periods delayed by 10-15 days. Heavy bleeding lasting 7-8 days. Associated mood swings, irritability. No inter-menstrual bleeding or post-coital bleeding.', 'Weight: 72 kg, Height: 5''4". BMI: 27.1. Thyroid: no palpable nodules. Breast exam: normal. P/A: soft, non-tender. P/S: cervix healthy, os closed. Bimanual: uterus anteverted, normal size, no adnexal masses.', 'Dysfunctional Uterine Bleeding (N93.8). Rule out PCOS and thyroid dysfunction. Overweight.', 'Investigations: Transvaginal USG, TSH, free T4, FSH, LH, Prolactin, DHEAS, Testosterone. Start Mefenamic acid 500mg TDS during menses for pain. Iron supplement Ferrous Sulphate 200mg OD. Diet counseling for weight management. Follow-up after reports.', 2, NOW() - INTERVAL '55 days', NOW(), 0),

    (3,  NOW() - INTERVAL '50 days', 'Patient Vihaan Singh, 46 years, presents with chest tightness and breathlessness on exertion for 3 weeks. Pain is central, non-radiating, and occurs during walking uphill or climbing stairs. No rest pain. History of smoking 10 cigarettes/day for 15 years. Family history: father had MI at age 55.', 'BP: 138/88 mmHg. HR: 88 bpm. SpO2: 97%. BMI: 28.2. JVP: not raised. Heart sounds: S1S2 normal, no added sounds. Lungs: bilateral air entry equal, no crackles/wheezing. ECG: Normal sinus rhythm, no ST-T changes. Troponin I: negative.', 'Unstable Angina - to rule out (I20.0). Hypertension Stage 1. Smoking-related cardiovascular risk.', 'Admit for observation and serial ECG monitoring. 2D Echo and TMT scheduled. Start Aspirin 75mg OD, Atorvastatin 40mg OD, Metoprolol 25mg BD. Strict smoking cessation counseling. Low-fat, low-salt diet. Review echo results and decide on angiography if needed.', 3, NOW() - INTERVAL '50 days', NOW(), 0),

    (4,  NOW() - INTERVAL '45 days', 'Mother brings 4-year-old Ananya for vaccination follow-up. Child had mild fever (100.2°F) for 2 days after DPT booster. Small red rash at injection site resolved in 3 days. Currently eating well, active, and playful.', 'Weight: 16.5 kg (50th percentile). Height: 102 cm (75th percentile). Temperature: 98.6°F. ENT: normal. Chest: clear. Abdomen: soft, non-tender. Injection site: healed, no induration. Development: age-appropriate milestones achieved.', 'Post-vaccination reaction - resolved. Normal growth and development.', 'No further medication needed for vaccination reaction. Next vaccination: Typhoid booster at 5 years. Continue balanced diet with fruits, vegetables, and milk. Encourage outdoor play for physical development. Annual dental checkup recommended. Follow-up at 5 years for booster.', 4, NOW() - INTERVAL '45 days', NOW(), 0),

    (5,  NOW() - INTERVAL '40 days', 'Patient Arjun Kumar, 42 years, reports recurring migraine headaches for 8 months. Frequency: 3-4 times/month. Duration: 6-12 hours. Unilateral throbbing headache, mainly on the right side. Associated with visual aura (zig-zag lines) 20 minutes before onset. Nausea and photophobia during episodes. Triggers: stress, lack of sleep, strong perfumes.', 'BP: 120/78 mmHg. HR: 72 bpm. Neurological exam: cranial nerves intact, no focal deficits. Fundoscopy: normal. No papilledema. Motor: 5/5 all limbs. Sensory: intact. Reflexes: 2+ symmetric. Romberg: negative.', 'Migraine with aura (G43.1). Frequency suggests need for prophylactic therapy.', 'Acute: Sumatriptan 50mg at onset of headache, may repeat after 2 hours if no relief (max 200mg/day). Prophylaxis: Start Propranolol 20mg BD. Maintain headache diary documenting frequency, duration, triggers. Sleep hygiene counseling (7-8 hours). Stress management: yoga/meditation recommended. Avoid identified triggers. MRI Brain if no improvement in 8 weeks.', 5, NOW() - INTERVAL '40 days', NOW(), 0),

    (6,  NOW() - INTERVAL '35 days', 'Patient Saanvi Gupta, 26 years, presents with persistent acne on face and back for 1 year. Worse before periods. Previous OTC treatments (benzoyl peroxide) partially effective. Associated oily skin. No family history of severe acne.', 'Face: Comedonal and inflammatory acne Grade III (Pillsbury). Multiple open and closed comedones on forehead and chin. Papules and pustules on cheeks. Back: scattered papules. No scarring. No hirsutism or alopecia.', 'Acne Vulgaris Grade III (L70.0). Possible hormonal component given premenstrual flare.', 'Topical: Adapalene 0.1% gel at night + Clindamycin 1% gel in morning. Oral: Doxycycline 100mg OD for 8 weeks. Salicylic acid face wash twice daily. Non-comedogenic sunscreen SPF 30+ daily. Avoid picking/squeezing lesions. If no improvement: consider hormonal evaluation and oral contraceptives. Review in 8 weeks.', 6, NOW() - INTERVAL '35 days', NOW(), 0),

    (7,  NOW() - INTERVAL '30 days', 'Patient Reyansh Joshi, 24 years, complains of right knee pain for 3 months. Pain worsens with climbing stairs and prolonged sitting. Occasional swelling after physical activity. History of football injury 6 months ago (landed awkwardly). No locking or giving way.', 'Right knee: mild effusion. Tenderness at medial joint line. McMurray test: positive for medial meniscus. Anterior drawer test: negative. Lachman test: negative. Valgus/varus stress: stable. Range of motion: 0-120° (limited by pain at terminal flexion). Left knee: normal.', 'Medial Meniscal Injury, Right Knee (S83.2). Possible partial tear.', 'MRI right knee to confirm diagnosis. Start physiotherapy: quadriceps strengthening, hamstring stretches. RICE protocol after activity. Tablet Etoricoxib 90mg OD for 2 weeks for pain. Knee cap/brace during physical activity. Avoid squatting and cross-legged sitting. If MRI confirms significant tear: discuss arthroscopic surgery options. Review with MRI in 2 weeks.', 7, NOW() - INTERVAL '30 days', NOW(), 0),

    (8,  NOW() - INTERVAL '25 days', 'Patient Ishita Nair, 36 years, reports anxiety episodes for 2 months. Episodes include sudden palpitations, sweating, trembling, feeling of impending doom lasting 10-15 minutes. Occurs 2-3 times/week. Difficulty sleeping (takes 1-2 hours to fall asleep). Recent job change and marital stress identified as precipitating factors.', 'Vitals stable. BP: 118/76 mmHg. HR: 84 bpm. No tremor. Thyroid: normal. Mental Status Exam: anxious appearance, oriented x3, coherent speech, no psychotic features, no suicidal ideation. PHQ-9 score: 12 (moderate depression). GAD-7 score: 15 (moderate-severe anxiety).', 'Generalized Anxiety Disorder with Panic Episodes (F41.0). Moderate depressive episode secondary to anxiety (F32.1). Insomnia related to anxiety (G47.0).', 'Start Escitalopram 10mg OD morning. Clonazepam 0.25mg SOS for acute panic attacks (max 2 per week). Sleep hygiene measures: fixed bedtime, no screens 1 hour before bed, warm milk. Cognitive Behavioral Therapy (CBT) referral - weekly sessions. Relaxation techniques: deep breathing exercises, progressive muscle relaxation. Reduce caffeine intake. Follow-up in 2 weeks for medication review.', 8, NOW() - INTERVAL '25 days', NOW(), 0),

    (9,  NOW() - INTERVAL '20 days', 'Patient Kabir Deshmukh, 49 years, presents with burning sensation during urination for 5 days. Increased urinary frequency (every 1-2 hours). Urgency with occasional incontinence. Mild lower abdominal discomfort. No fever, no hematuria. No previous UTI history. No known diabetes.', 'Temperature: 98.4°F. BP: 130/82 mmHg. Abdomen: mild suprapubic tenderness. No CVA tenderness. External genitalia: normal. Digital rectal exam: prostate mildly enlarged, non-tender, no nodules. Urine dipstick: Leukocytes ++, Nitrites +, Blood trace.', 'Urinary Tract Infection, uncomplicated (N39.0). Benign Prostatic Hyperplasia, mild (N40.0).', 'Urine C&S sent. Start empirical antibiotics: Tab Nitrofurantoin 100mg BD for 7 days. Increase water intake to 3-4 liters/day. Cranberry juice recommended. Avoid caffeine and alcohol. PSA test and USG KUB ordered. If symptoms persist after antibiotics: consider urology follow-up for BPH evaluation. Review with C&S results in 3 days.', 9, NOW() - INTERVAL '20 days', NOW(), 0),

    (10, NOW() - INTERVAL '15 days', 'Patient Myra Iyer, 32 years, follow-up for hypothyroidism diagnosed 1 year ago. Currently on Levothyroxine 50mcg daily. Reports weight gain of 5kg in 3 months despite diet control. Persistent fatigue, hair thinning, and dry skin. Constipation 2-3 times/week. Menstrual cycles regular.', 'Weight: 68 kg (was 63 kg 3 months ago). BP: 110/72 mmHg. HR: 64 bpm. Skin: dry, mild periorbital puffiness. Hair: diffuse thinning, no alopecia areata. Thyroid: no palpable nodules. Reflexes: mildly delayed relaxation phase. TSH: 8.4 mIU/L (elevated). Free T4: 0.8 ng/dL (low-normal).', 'Inadequately controlled Hypothyroidism (E03.9). Dose adjustment needed.', 'Increase Levothyroxine to 75mcg daily on empty stomach, 30 minutes before breakfast. Repeat TSH and free T4 after 6 weeks. Add Biotin 5000mcg daily for hair health. Coconut oil for dry skin. High-fiber diet for constipation. Avoid soy, calcium supplements within 4 hours of thyroid medication. If TSH not normalized: consider evaluation for absorption issues. Follow-up in 6 weeks with reports.', 10, NOW() - INTERVAL '15 days', NOW(), 0),

    (11, NOW() - INTERVAL '12 days', 'Patient Aditya Verma, 37 years, presents with heartburn and epigastric pain for 3 weeks. Pain worse after spicy food and at night when lying down. Sour taste in mouth. No dysphagia, no weight loss, no hematemesis. Lifestyle: irregular meal timings, heavy dinner, sleeps within 1 hour of eating.', 'BMI: 25.8. Abdomen: mild epigastric tenderness, no guarding. No hepatosplenomegaly. Bowel sounds: normal.', 'Gastroesophageal Reflux Disease (K21.0). Rule out H. pylori infection.', 'Start Pantoprazole 40mg OD before breakfast for 8 weeks. Domperidone 10mg before meals TDS. H. pylori stool antigen test ordered. Lifestyle modifications: Elevate head of bed 6 inches. Eat dinner 3 hours before bedtime. Avoid spicy, oily, acidic foods. Small frequent meals. Quit smoking and reduce alcohol. Follow-up in 4 weeks.', 11, NOW() - INTERVAL '12 days', NOW(), 0),

    (12, NOW() - INTERVAL '10 days', 'Patient Kiara Chopra, 31 years, complains of persistent dry cough for 6 weeks. Initially associated with cold, but cough persists. Worse at night and early morning. No hemoptysis, no fever, no weight loss. No history of asthma or allergies. Non-smoker. Recently renovated house (possible dust exposure).', 'SpO2: 98%. Chest: bilateral vesicular breath sounds, no wheezing or crackles on auscultation. PFT: FEV1/FVC ratio normal. Chest X-ray: clear lung fields, no consolidation or effusion.', 'Post-nasal drip syndrome (J31.0). Allergic rhinitis with cough variant (J30.4). Rule out early asthma.', 'Tab Montelukast 10mg at night for 4 weeks. Nasal spray Fluticasone 2 puffs each nostril daily. Antihistamine Cetirizine 10mg at night. Steam inhalation twice daily. Avoid dust exposure, use air purifier at home. If cough persists: methacholine challenge test for cough-variant asthma. Follow-up in 4 weeks.', 12, NOW() - INTERVAL '10 days', NOW(), 0),

    (13, NOW() - INTERVAL '8 days', 'Patient Vivaan Tiwari, 19 years, referred for elevated creatinine found on routine blood work. No specific symptoms. Mild bilateral pedal edema noticed by mother. Urine output normal. No dysuria or hematuria. Family history: father on dialysis for diabetic nephropathy.', 'BP: 142/88 mmHg (elevated for age). Weight: 65 kg. Bilateral pitting pedal edema grade 1. No ascites. Kidneys: not palpable. Creatinine: 1.8 mg/dL. BUN: 28 mg/dL. Urine routine: Protein ++, no RBCs.', 'Chronic Kidney Disease Stage 2-3 (N18.3). Proteinuria. Secondary hypertension. Need further workup to determine etiology.', 'Urgent investigations: 24-hour urine protein, serum albumin, USG abdomen, ANA, C3/C4, serum electrophoresis, HbA1c, lipid profile. Start ACE inhibitor Ramipril 2.5mg OD for BP and renoprotection. Low-protein diet (0.8g/kg/day). Restrict salt. Avoid NSAIDs and nephrotoxic drugs. Nephrology follow-up in 1 week with all reports. May need renal biopsy based on results.', 13, NOW() - INTERVAL '8 days', NOW(), 0),

    (14, NOW() - INTERVAL '6 days', 'Patient Aisha Khan, 28 years, routine eye checkup. Complains of eye strain and headaches after prolonged computer work (8+ hours daily). Difficulty reading small text on phone. Last eye checkup 3 years ago. Uses no corrective lenses.', 'Visual acuity: Right eye 6/9, Left eye 6/12. Near vision: N8 both eyes. IOP: Right 14 mmHg, Left 15 mmHg (normal). Slit lamp: anterior segment normal bilaterally. Fundus: normal, no diabetic or hypertensive changes. Cover test: orthophoria at distance, mild exophoria at near.', 'Myopia, mild bilateral (H52.1). Digital Eye Strain / Computer Vision Syndrome (H53.1). Convergence insufficiency, mild.', 'Prescribe corrective spectacles: Right -0.75D sph, Left -1.00D sph. Anti-reflective coating recommended. 20-20-20 rule for screen time: every 20 minutes, look at something 20 feet away for 20 seconds. Lubricating eye drops: Carboxymethylcellulose 0.5% QID. Reduce screen brightness, use night mode. Convergence exercises at home. Annual eye checkup recommended.', 14, NOW() - INTERVAL '6 days', NOW(), 0),

    (15, NOW() - INTERVAL '4 days', 'Patient Dhruv Mehta, 44 years, presents with left ear pain for 4 days. Started after swimming. Associated reduced hearing, feeling of fullness. Mild watery discharge. No fever. No history of ear infections. No tinnitus or vertigo.', 'Left ear: ear canal erythematous and edematous. Tragal tenderness positive. Tympanic membrane: obscured by debris. After gentle cleaning: TM intact, slightly injected. Right ear: normal. Rinne test: positive bilaterally. Weber: lateralizes to left.', 'Acute Otitis Externa, Left Ear (H60.5). Swimmer''s ear. Mild conductive hearing loss secondary to canal edema.', 'Ear drops: Ofloxacin 0.3% + Dexamethasone ear drops, 4 drops in left ear TDS for 7 days. Tab Ibuprofen 400mg SOS for pain. Keep ear dry - use cotton ball with petroleum jelly while bathing. No swimming for 2 weeks. No ear buds or cotton swabs. If no improvement in 5 days or fever develops: return for review. Hearing recheck after treatment completion.', 15, NOW() - INTERVAL '4 days', NOW(), 0)
    ON CONFLICT DO NOTHING;

-- ============================================================
-- 8. PAYMENTS (For Completed Appointments - 15 payments)
-- ============================================================

INSERT INTO payments (id, payment_intent_id, stripe_charge_id, amount, currency, status, payment_method, description, receipt_url, user_id, appointment_id, paid_at, created_at, updated_at, version)
VALUES
    (1,  'pi_demo_001', 'ch_demo_001', 500.00,  'INR', 'SUCCEEDED', 'CARD', 'Consultation with Dr. Rajesh Sharma - General Practice',       'https://receipt.stripe.com/demo1',  200, 1,  NOW() - INTERVAL '60 days', NOW() - INTERVAL '61 days', NOW(), 0),
    (2,  'pi_demo_002', 'ch_demo_002', 800.00,  'INR', 'SUCCEEDED', 'CARD', 'Consultation with Dr. Priya Patel - Obstetrics & Gynaecology', 'https://receipt.stripe.com/demo2',  201, 2,  NOW() - INTERVAL '55 days', NOW() - INTERVAL '56 days', NOW(), 0),
    (3,  'pi_demo_003', 'ch_demo_003', 1200.00, 'INR', 'SUCCEEDED', 'CARD', 'Consultation with Dr. Amit Rathore - Cardiology',              'https://receipt.stripe.com/demo3',  202, 3,  NOW() - INTERVAL '50 days', NOW() - INTERVAL '51 days', NOW(), 0),
    (4,  'pi_demo_004', 'ch_demo_004', 700.00,  'INR', 'SUCCEEDED', 'CARD', 'Consultation with Dr. Sunita Gupta - Pediatrics',              'https://receipt.stripe.com/demo4',  203, 4,  NOW() - INTERVAL '45 days', NOW() - INTERVAL '46 days', NOW(), 0),
    (5,  'pi_demo_005', 'ch_demo_005', 1000.00, 'INR', 'SUCCEEDED', 'CARD', 'Consultation with Dr. Vikram Mehta - Neurology',               'https://receipt.stripe.com/demo5',  204, 5,  NOW() - INTERVAL '40 days', NOW() - INTERVAL '41 days', NOW(), 0),
    (6,  'pi_demo_006', 'ch_demo_006', 600.00,  'INR', 'SUCCEEDED', 'CARD', 'Consultation with Dr. Ananya K - Dermatology',                 'https://receipt.stripe.com/demo6',  205, 6,  NOW() - INTERVAL '35 days', NOW() - INTERVAL '36 days', NOW(), 0),
    (7,  'pi_demo_007', 'ch_demo_007', 900.00,  'INR', 'SUCCEEDED', 'CARD', 'Consultation with Dr. Sanjay Verma - Orthopaedics',            'https://receipt.stripe.com/demo7',  206, 7,  NOW() - INTERVAL '30 days', NOW() - INTERVAL '31 days', NOW(), 0),
    (8,  'pi_demo_008', 'ch_demo_008', 1500.00, 'INR', 'SUCCEEDED', 'CARD', 'Consultation with Dr. Deepa Nair - Psychiatry',                'https://receipt.stripe.com/demo8',  207, 8,  NOW() - INTERVAL '25 days', NOW() - INTERVAL '26 days', NOW(), 0),
    (9,  'pi_demo_009', 'ch_demo_009', 800.00,  'INR', 'SUCCEEDED', 'CARD', 'Consultation with Dr. Arjun Reddy - Urology',                  'https://receipt.stripe.com/demo9',  208, 9,  NOW() - INTERVAL '20 days', NOW() - INTERVAL '21 days', NOW(), 0),
    (10, 'pi_demo_010', 'ch_demo_010', 700.00,  'INR', 'SUCCEEDED', 'CARD', 'Consultation with Dr. Kavitha Iyer - Endocrinology',            'https://receipt.stripe.com/demo10', 209, 10, NOW() - INTERVAL '15 days', NOW() - INTERVAL '16 days', NOW(), 0),
    (11, 'pi_demo_011', 'ch_demo_011', 600.00,  'INR', 'SUCCEEDED', 'CARD', 'Consultation with Dr. Manoj Tiwari - Gastroenterology',         'https://receipt.stripe.com/demo11', 210, 11, NOW() - INTERVAL '12 days', NOW() - INTERVAL '13 days', NOW(), 0),
    (12, 'pi_demo_012', 'ch_demo_012', 800.00,  'INR', 'SUCCEEDED', 'CARD', 'Consultation with Dr. Neha Agarwal - Pulmonology',              'https://receipt.stripe.com/demo12', 211, 12, NOW() - INTERVAL '10 days', NOW() - INTERVAL '11 days', NOW(), 0),
    (13, 'pi_demo_013', 'ch_demo_013', 1100.00, 'INR', 'SUCCEEDED', 'CARD', 'Consultation with Dr. Karthik S - Nephrology',                  'https://receipt.stripe.com/demo13', 212, 13, NOW() - INTERVAL '8 days',  NOW() - INTERVAL '9 days',  NOW(), 0),
    (14, 'pi_demo_014', 'ch_demo_014', 500.00,  'INR', 'SUCCEEDED', 'CARD', 'Consultation with Dr. Pooja Joshi - Ophthalmology',              'https://receipt.stripe.com/demo14', 213, 14, NOW() - INTERVAL '6 days',  NOW() - INTERVAL '7 days',  NOW(), 0),
    (15, 'pi_demo_015', 'ch_demo_015', 600.00,  'INR', 'SUCCEEDED', 'CARD', 'Consultation with Dr. Rahul Deshmukh - ENT',                    'https://receipt.stripe.com/demo15', 214, 15, NOW() - INTERVAL '4 days',  NOW() - INTERVAL '5 days',  NOW(), 0)
    ON CONFLICT (payment_intent_id) DO NOTHING;

-- ============================================================
-- 9. INVOICES (For Completed Appointments - 15 invoices)
-- ============================================================

INSERT INTO invoices (id, invoice_number, subtotal, tax, total, currency, status, user_id, appointment_id, payment_id, description, issue_date, due_date, paid_date, created_at, updated_at, version)
VALUES
    (1,  'INV-2025-0001', 500.00,  90.00,   590.00,  'INR', 'PAID', 200, 1,  1,  'General Practice Consultation - Dr. Rajesh Sharma',          NOW() - INTERVAL '61 days', NOW() - INTERVAL '46 days', NOW() - INTERVAL '60 days', NOW() - INTERVAL '61 days', NOW(), 0),
    (2,  'INV-2025-0002', 800.00,  144.00,  944.00,  'INR', 'PAID', 201, 2,  2,  'Obstetrics & Gynaecology Consultation - Dr. Priya Patel',    NOW() - INTERVAL '56 days', NOW() - INTERVAL '41 days', NOW() - INTERVAL '55 days', NOW() - INTERVAL '56 days', NOW(), 0),
    (3,  'INV-2025-0003', 1200.00, 216.00,  1416.00, 'INR', 'PAID', 202, 3,  3,  'Cardiology Consultation - Dr. Amit Rathore',                 NOW() - INTERVAL '51 days', NOW() - INTERVAL '36 days', NOW() - INTERVAL '50 days', NOW() - INTERVAL '51 days', NOW(), 0),
    (4,  'INV-2025-0004', 700.00,  126.00,  826.00,  'INR', 'PAID', 203, 4,  4,  'Pediatrics Consultation - Dr. Sunita Gupta',                 NOW() - INTERVAL '46 days', NOW() - INTERVAL '31 days', NOW() - INTERVAL '45 days', NOW() - INTERVAL '46 days', NOW(), 0),
    (5,  'INV-2025-0005', 1000.00, 180.00,  1180.00, 'INR', 'PAID', 204, 5,  5,  'Neurology Consultation - Dr. Vikram Mehta',                  NOW() - INTERVAL '41 days', NOW() - INTERVAL '26 days', NOW() - INTERVAL '40 days', NOW() - INTERVAL '41 days', NOW(), 0),
    (6,  'INV-2025-0006', 600.00,  108.00,  708.00,  'INR', 'PAID', 205, 6,  6,  'Dermatology Consultation - Dr. Ananya Krishnamurthy',        NOW() - INTERVAL '36 days', NOW() - INTERVAL '21 days', NOW() - INTERVAL '35 days', NOW() - INTERVAL '36 days', NOW(), 0),
    (7,  'INV-2025-0007', 900.00,  162.00,  1062.00, 'INR', 'PAID', 206, 7,  7,  'Orthopaedics Consultation - Dr. Sanjay Verma',               NOW() - INTERVAL '31 days', NOW() - INTERVAL '16 days', NOW() - INTERVAL '30 days', NOW() - INTERVAL '31 days', NOW(), 0),
    (8,  'INV-2025-0008', 1500.00, 270.00,  1770.00, 'INR', 'PAID', 207, 8,  8,  'Psychiatry Consultation - Dr. Deepa Nair',                   NOW() - INTERVAL '26 days', NOW() - INTERVAL '11 days', NOW() - INTERVAL '25 days', NOW() - INTERVAL '26 days', NOW(), 0),
    (9,  'INV-2025-0009', 800.00,  144.00,  944.00,  'INR', 'PAID', 208, 9,  9,  'Urology Consultation - Dr. Arjun Reddy',                    NOW() - INTERVAL '21 days', NOW() - INTERVAL '6 days',  NOW() - INTERVAL '20 days', NOW() - INTERVAL '21 days', NOW(), 0),
    (10, 'INV-2025-0010', 700.00,  126.00,  826.00,  'INR', 'PAID', 209, 10, 10, 'Endocrinology Consultation - Dr. Kavitha Iyer',              NOW() - INTERVAL '16 days', NOW() - INTERVAL '1 day',   NOW() - INTERVAL '15 days', NOW() - INTERVAL '16 days', NOW(), 0),
    (11, 'INV-2025-0011', 600.00,  108.00,  708.00,  'INR', 'PAID', 210, 11, 11, 'Gastroenterology Consultation - Dr. Manoj Tiwari',           NOW() - INTERVAL '13 days', NOW() + INTERVAL '2 days',  NOW() - INTERVAL '12 days', NOW() - INTERVAL '13 days', NOW(), 0),
    (12, 'INV-2025-0012', 800.00,  144.00,  944.00,  'INR', 'PAID', 211, 12, 12, 'Pulmonology Consultation - Dr. Neha Agarwal',                NOW() - INTERVAL '11 days', NOW() + INTERVAL '4 days',  NOW() - INTERVAL '10 days', NOW() - INTERVAL '11 days', NOW(), 0),
    (13, 'INV-2025-0013', 1100.00, 198.00,  1298.00, 'INR', 'PAID', 212, 13, 13, 'Nephrology Consultation - Dr. Karthik Subramanian',          NOW() - INTERVAL '9 days',  NOW() + INTERVAL '6 days',  NOW() - INTERVAL '8 days',  NOW() - INTERVAL '9 days',  NOW(), 0),
    (14, 'INV-2025-0014', 500.00,  90.00,   590.00,  'INR', 'PAID', 213, 14, 14, 'Ophthalmology Consultation - Dr. Pooja Joshi',               NOW() - INTERVAL '7 days',  NOW() + INTERVAL '8 days',  NOW() - INTERVAL '6 days',  NOW() - INTERVAL '7 days',  NOW(), 0),
    (15, 'INV-2025-0015', 600.00,  108.00,  708.00,  'INR', 'PAID', 214, 15, 15, 'ENT Consultation - Dr. Rahul Deshmukh',                      NOW() - INTERVAL '5 days',  NOW() + INTERVAL '10 days', NOW() - INTERVAL '4 days',  NOW() - INTERVAL '5 days',  NOW(), 0),

-- Pending Invoices (for scheduled appointments)
    (16, 'INV-2025-0016', 400.00,  72.00,   472.00,  'INR', 'PENDING', 215, 16, NULL, 'Dental Consultation - Dr. Meena Yadav',                NOW(),                      NOW() + INTERVAL '15 days', NULL,                       NOW(),                      NOW(), 0),
    (17, 'INV-2025-0017', 1000.00, 180.00,  1180.00, 'INR', 'PENDING', 216, 17, NULL, 'General Surgery Consultation - Dr. Suresh Pillai',     NOW(),                      NOW() + INTERVAL '15 days', NULL,                       NOW(),                      NOW(), 0),
    (18, 'INV-2025-0018', 900.00,  162.00,  1062.00, 'INR', 'PENDING', 217, 18, NULL, 'Rheumatology Consultation - Dr. Rashmi Saxena',        NOW(),                      NOW() + INTERVAL '15 days', NULL,                       NOW(),                      NOW(), 0)
    ON CONFLICT (invoice_number) DO NOTHING;

-- ============================================================
-- 10. UPDATE SEQUENCES to avoid conflicts
-- ============================================================

SELECT setval('users_seq', (SELECT COALESCE(MAX(id), 1) FROM users));
SELECT setval('patients_seq', (SELECT COALESCE(MAX(id), 1) FROM patients));
SELECT setval('doctors_seq', (SELECT COALESCE(MAX(id), 1) FROM doctors));
SELECT setval('appointments_seq', (SELECT COALESCE(MAX(id), 1) FROM appointments));
SELECT setval('consultations_seq', (SELECT COALESCE(MAX(id), 1) FROM consultations));
SELECT setval('payments_seq', (SELECT COALESCE(MAX(id), 1) FROM payments));
SELECT setval('invoices_seq', (SELECT COALESCE(MAX(id), 1) FROM invoices));