use indicatif::{ProgressBar, ProgressStyle};
use once_cell::sync::Lazy;
use std::io::{self, Read};
use std::net::{Shutdown, TcpListener, TcpStream};
use std::sync::atomic::{AtomicBool, AtomicU64, Ordering};
use std::sync::Mutex;
use std::thread;

pub(crate) fn start() -> Result<(), Box<dyn std::error::Error>> {
    let listener = TcpListener::bind("127.0.0.1:20181")?;

    println!("Server started on {}", listener.local_addr()?);

    for stream in listener.incoming() {
        match stream {
            Ok(stream) => {
                println!("Client connected!");
                handle_client(stream)?;
                println!("Client disconnected!");
                break; // 只处理一个客户端
            }
            Err(e) => {
                eprintln!("Connection failed: {}", e);
                continue;
            }
        }
    }
    Ok(())
}

static NEXT_PROCESS: AtomicU64 = AtomicU64::new(0);
static RUNNING: AtomicBool = AtomicBool::new(true);
static PROGRESS_BAR: Lazy<Mutex<ProgressBar>> = Lazy::new(|| {
    let pb = ProgressBar::new(100);
    pb.set_style(ProgressStyle::default_bar()
        .template("{spinner:.green} {msg} [{elapsed_precise}] [{wide_bar:.white}] {pos:>7}/{len:7} {percent:>7}%").unwrap()
        .progress_chars("#-"));
    Mutex::new(pb)
});

fn handle_client(mut stream: TcpStream) -> io::Result<()> {
    let mut buffer = [0u8; 1024];
    thread::spawn(move || {
        while RUNNING.load(Ordering::SeqCst) {
            let pb = PROGRESS_BAR.lock().unwrap();
            let position = NEXT_PROCESS.load(Ordering::SeqCst);
            pb.set_position(position);
            drop(pb); // 显式释放锁
            thread::sleep(std::time::Duration::from_millis(100)); // 控制更新频率
        }
        println!("Output thread exiting");
    });
    let mut logs: Vec<String> = Vec::new();
    while RUNNING.load(Ordering::SeqCst) {
        match stream.read(&mut buffer)? {
            0 => {
                logs.iter().for_each(|l| {
                    print!("{}", l);
                });
                println!("Connection closed by the client.");
                break;
            }
            prediction_size => {
                let message = std::str::from_utf8(&buffer[..prediction_size]).unwrap().trim_end_matches("\r\n");
                let head = if message.len() > 3 { &message[0..2] } else { message };
                let body = if message.contains("=>") { &message[4..] } else { message };
                let pb = PROGRESS_BAR.lock().unwrap();
                match head {
                    "S1" => {
                        pb.reset();
                        pb.set_message("mapping");
                    }
                    "S2" => {
                        pb.reset();
                        pb.set_message("transformation");
                    }
                    "P1" | "P2" => {
                        if let Ok(value) = body.parse::<f32>() {
                            NEXT_PROCESS.store(value as u64, Ordering::SeqCst)
                        }
                    }
                    "E1" | "E2" => {
                        pb.finish_and_clear();
                    }
                    "LG" => {
                        if let Ok(value) = body.parse::<String>() {
                            let line = format!("\x1b[2K\r{}\n", value.trim_end_matches('\n'));
                            if !(line.contains("=>") || line.len() <= 2) {
                                logs.push(line);
                            }
                        }
                    }
                    "ED" => {
                        logs.iter().for_each(|l| {
                            print!("{}", l);
                        });
                        pb.finish_with_message("completed");
                        RUNNING.store(false, Ordering::SeqCst);
                        break;
                    }
                    _ => {}
                }
                drop(pb);
            }
        }
    }
    println!("Press Enter to exit...");
    let mut input = String::new();
    io::stdin().read_line(&mut input).expect("Failed to read line");
    stream.shutdown(Shutdown::Both)?;
    Ok(())
}
